package net.stockkid.stockkidbe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.AuthDTO;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private JwtUtil jwtUtil;

    public ApiLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        log.info("ApiLoginFilter------------------------");
        log.info("attemptAuthentication");

        try {
            StringBuilder requestBody = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            AuthDTO authDTO = new ObjectMapper().readValue(requestBody.toString(), AuthDTO.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

            return getAuthenticationManager().authenticate(authToken);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authResult) throws IOException, ServletException {

        log.info("successfulAuthentication : " + authResult);
        log.info(authResult.getPrincipal());

        String username = ((User) authResult.getPrincipal()).getUsername();
        //get GrantedAuthority from Collection and remove ROLE_ prefix
        String role = authResult.getAuthorities().iterator().next().getAuthority().substring(5);

        try {
            String token = jwtUtil.generateToken(username, role);

            log.info("successful token : " + token);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json;charset=utf-8");

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setApiStatus(ResponseStatus.LOGIN_OK);
            responseDTO.setApiMsg("Login OK");
            responseDTO.setApiObj(token);

            String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

            PrintWriter writer = response.getWriter();
            writer.print(jsonBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
