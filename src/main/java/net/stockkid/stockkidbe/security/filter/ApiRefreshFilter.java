package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class ApiRefreshFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher antPathRequestMatcher;
    private final IoUtil ioUtil;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiRefreshFilter---------------------");

            response.setContentType("application/json;charset=utf-8");

            try {
                TokensDTO tokensDTO = ioUtil.readRequestBody(request, TokensDTO.class);

                JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtractToken(tokensDTO.getRefreshToken());

                if (new AntPathRequestMatcher("/api/refresh/tokens").matches(request)) {
                    TokensDTO newTokensDTO = memberService.rotateTokens(jwtClaimsDTO.getUsername(), jwtClaimsDTO.getRole(), jwtClaimsDTO.getSocial(), tokensDTO.getRefreshToken());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.REFRESH_OK, "Refresh OK", newTokensDTO);

                    ioUtil.writeResponseBody(response, responseDTO);
                } else if (new AntPathRequestMatcher("/api/refresh/logout").matches(request)) {
                    memberService.invalidateToken(jwtClaimsDTO.getUsername(), tokensDTO.getRefreshToken());

                    response.setStatus(HttpServletResponse.SC_OK);
                    ResponseDTO responseDTO = new ResponseDTO();
                    responseDTO.setApiStatus(ResponseStatus.LOGOUT_OK);
                    responseDTO.setApiMsg("Logout OK");

                    ioUtil.writeResponseBody(response, responseDTO);
                }
                return;
            } catch (Exception e) {
                log.info(e.getMessage());

                ResponseDTO responseDTO = new ResponseDTO();
                if (new AntPathRequestMatcher("/api/refresh/tokens").matches(request)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    responseDTO.setApiStatus(ResponseStatus.REFRESH_FAIL);
                } else if (new AntPathRequestMatcher("/api/refresh/logout").matches(request)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
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
