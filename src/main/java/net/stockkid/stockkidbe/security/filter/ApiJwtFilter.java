package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

@Log4j2
public class ApiJwtFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private JwtUtil jwtUtil;

    public ApiJwtFilter(AntPathRequestMatcher antPathRequestMatcher) {
        super(antPathRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("ApiJWTFilter---------------------");
        log.info("ApiJWTFilter---------------------");

        try {
            String token = extractTokenFromHeader(request);
            JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtract(token);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(jwtClaimsDTO.getUsername(), null, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + jwtClaimsDTO.getRole())));

            return usernamePasswordAuthenticationToken;
        } catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {

        String authHeader = request.getHeader("authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer")) {
            log.info("Authorization : " + authHeader);
            return authHeader.substring(7);
        }
        return null;
    }
}
