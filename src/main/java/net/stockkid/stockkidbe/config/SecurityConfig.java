package net.stockkid.stockkidbe.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.repository.MemberRepository;
import net.stockkid.stockkidbe.security.filter.ApiJwtFilter;
import net.stockkid.stockkidbe.security.filter.ApiLoginFilter;
import net.stockkid.stockkidbe.security.handler.ApiJwtFailureHandler;
import net.stockkid.stockkidbe.security.handler.ApiJwtSuccessHandler;
import net.stockkid.stockkidbe.security.handler.ApiLoginFailureHandler;
import net.stockkid.stockkidbe.security.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;

    @Bean
    static RoleHierarchy roleHierarchy() {
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(MemberRole.getRoleHierarchy());

        return hierarchy;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/member/signup").permitAll()
                        .requestMatchers("/api/jwt/member/changePassword").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiJwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf((csrf) -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(new UserDetailsServiceImpl(memberRepository));
        return new ProviderManager(Arrays.asList(daoAuthenticationProvider));
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() throws Exception {

        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/member/login");
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailureHandler());

        return apiLoginFilter;
    }

    @Bean
    public ApiJwtFilter apiJwtFilter() throws Exception {

        ApiJwtFilter apiJwtFilter = new ApiJwtFilter(new AntPathRequestMatcher("/api/jwt/**"));
        apiJwtFilter.setAuthenticationManager(authenticationManager());
        apiJwtFilter.setAuthenticationSuccessHandler(new ApiJwtSuccessHandler());
        apiJwtFilter.setAuthenticationFailureHandler(new ApiJwtFailureHandler());

        return apiJwtFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Key", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
