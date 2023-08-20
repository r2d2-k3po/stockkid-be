package net.stockkid.stockkidbe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import net.stockkid.stockkidbe.security.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiRefreshFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenUtil tokenUtil;

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
                StringBuilder requestBody = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
                TokensDTO tokensDTO = new ObjectMapper().readValue(requestBody.toString(), TokensDTO.class);

                JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtractRefreshToken(tokensDTO.getRefreshToken());

                TokensDTO newTokesnDTO = tokenUtil.rotateTokens(jwtClaimsDTO.getUsername(), jwtClaimsDTO.getRole(), jwtClaimsDTO.getSocial(), tokensDTO.getRefreshToken());

                response.setStatus(HttpServletResponse.SC_CREATED);

                ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.REFRESH_OK, "Refresh OK", newTokesnDTO);
                String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

                PrintWriter writer = response.getWriter();
                writer.print(jsonBody);
                return;
            } catch (Exception e) {
                log.info(e.getMessage());

                response.setStatus(HttpServletResponse.SC_CONFLICT);

                ResponseDTO responseDTO = new ResponseDTO();
                responseDTO.setApiStatus(ResponseStatus.REFRESH_FAIL);
                responseDTO.setApiMsg(e.getMessage());

                String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

                PrintWriter writer = response.getWriter();
                writer.print(jsonBody);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}
