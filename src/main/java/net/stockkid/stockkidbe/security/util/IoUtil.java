package net.stockkid.stockkidbe.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
@Component
public class IoUtil {

    public <T> T readRequestBody(HttpServletRequest request, Class<T> valueType) throws IOException {

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return new ObjectMapper().readValue(requestBody.toString(), valueType);
    }

    public void writeResponseBody(HttpServletResponse response, ResponseDTO responseDTO) throws IOException {

        response.setContentType("application/json;charset=utf-8");

        String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

        PrintWriter writer = response.getWriter();
        writer.print(jsonBody);
    }
}
