package net.stockkid.stockkidbe.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiJWTFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.info("jwt failure handler................");
        log.info(exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setApiStatus(ResponseStatus.JWT_FAIL);
        responseDTO.setApiMsg(exception.getMessage());

        String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

        PrintWriter writer = response.getWriter();
        writer.print(jsonBody);
    }
}
