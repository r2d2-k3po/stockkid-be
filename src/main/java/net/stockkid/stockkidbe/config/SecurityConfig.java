package net.stockkid.stockkidbe.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.repository.MemberRepository;
import net.stockkid.stockkidbe.security.filter.*;
import net.stockkid.stockkidbe.security.handler.ApiLoginFailureHandler;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import net.stockkid.stockkidbe.service.MemberService;
import net.stockkid.stockkidbe.service.MemberServiceImpl;
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
import java.util.List;

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
                        .requestMatchers("/permit/**").permitAll()
                        .requestMatchers("/access/**").hasRole("USER")
                        .anyRequest().denyAll()
                )
                .addFilterBefore(apiAccessFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiRefreshFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiGoogleFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiNaverFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiKakaoFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf((csrf) -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    IoUtil ioUtil() { return new IoUtil(); }

    @Bean
    JwtUtil jwtUtil() { return new JwtUtil(); }

    @Bean
    MemberService memberService() {
        return new MemberServiceImpl(memberRepository, passwordEncoder(), jwtUtil());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(memberService());
        return new ProviderManager(List.of(daoAuthenticationProvider));
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() {

        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/login", ioUtil(), memberService());
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailureHandler());

        return apiLoginFilter;
    }

    @Bean
    public ApiAccessFilter apiAccessFilter() {
        return new ApiAccessFilter(new AntPathRequestMatcher("/access/**"), ioUtil(), jwtUtil());
    }

    @Bean
    public ApiRefreshFilter apiRefreshFilter() {
        return new ApiRefreshFilter(new AntPathRequestMatcher("/refresh/**"), ioUtil(), jwtUtil(), memberService());
    }

    @Bean
    public ApiGoogleFilter apiGoogleFilter() {
        return new ApiGoogleFilter(new AntPathRequestMatcher("/google/**"), ioUtil(), memberService());
    }

    @Bean
    public ApiNaverFilter apiNaverFilter() {
        return new ApiNaverFilter(new AntPathRequestMatcher("/naver/**"), ioUtil(), memberService());
    }

    @Bean
    public ApiKakaoFilter apiKakaoFilter() {
        return new ApiKakaoFilter(new AntPathRequestMatcher("/kakao/**"), ioUtil(), jwtUtil(), memberService());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Key", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
