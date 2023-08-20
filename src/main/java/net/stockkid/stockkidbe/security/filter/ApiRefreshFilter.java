package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import net.stockkid.stockkidbe.security.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ApiRefreshFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private IoUtil ioUtil;

    private final AntPathRequestMatcher antPathRequestMatcher;

    public ApiRefreshFilter(AntPathRequestMatcher antPathRequestMatcher) {
        this.antPathRequestMatcher = antPathRequestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiRefreshFilter---------------------");

            response.setContentType("application/json;charset=utf-8");

            try {
                TokensDTO tokensDTO = ioUtil.readRequestBody(request, TokensDTO.class);

                JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtractRefreshToken(tokensDTO.getRefreshToken());

                if (new AntPathRequestMatcher("/api/refresh/tokens").matches(request)) {
                    TokensDTO newTokesnDTO = tokenUtil.rotateTokens(jwtClaimsDTO.getUsername(), jwtClaimsDTO.getRole(), jwtClaimsDTO.getSocial(), tokensDTO.getRefreshToken());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.REFRESH_OK, "Refresh OK", newTokesnDTO);

                    ioUtil.writeResponseBody(response, responseDTO);
                } else if (new AntPathRequestMatcher("/api/refresh/logout").matches(request)) {
                    tokenUtil.invalidateToken(jwtClaimsDTO.getUsername());

                    response.setStatus(HttpServletResponse.SC_OK);
                    ResponseDTO responseDTO = new ResponseDTO();
                    responseDTO.setApiStatus(ResponseStatus.LOGOUT_OK);
                    responseDTO.setApiMsg("Logout OK");

                    ioUtil.writeResponseBody(response, responseDTO);
                }
                return;
            } catch (Exception e) {
                log.info(e.getMessage());

                response.setStatus(HttpServletResponse.SC_CONFLICT);

                ResponseDTO responseDTO = new ResponseDTO();
                if (new AntPathRequestMatcher("/api/refresh/tokens").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.REFRESH_FAIL);
                } else if (new AntPathRequestMatcher("/api/refresh/logout").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.LOGOUT_FAIL);
                }
                responseDTO.setApiMsg(e.getMessage());

                ioUtil.writeResponseBody(response, responseDTO);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}
