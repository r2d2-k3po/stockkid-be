package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContext;

import java.io.IOException;
import java.util.Collections;

@Log4j2
public class ApiAccessFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IoUtil ioUtil;

    private final AntPathRequestMatcher antPathRequestMatcher;

    public ApiAccessFilter(AntPathRequestMatcher antPathRequestMatcher) {
        this.antPathRequestMatcher = antPathRequestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("RequestURI : " + request.getRequestURI());

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiAccessFilter---------------------");

            try {
                String accessToken = extractTokenFromHeader(request);
                JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtractAccessToken(accessToken);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(jwtClaimsDTO.getUsername(), null, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + jwtClaimsDTO.getRole())));

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(usernamePasswordAuthenticationToken);
                SecurityContextHolder.setContext(context);

                log.info("setting SecurityContext...........");

                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                log.info(e.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                ResponseDTO responseDTO = new ResponseDTO();
                responseDTO.setApiStatus(ResponseStatus.ACCESS_FAIL);
                responseDTO.setApiMsg(e.getMessage());

                ioUtil.writeResponseBody(response, responseDTO);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {

        String authHeader = request.getHeader("authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
