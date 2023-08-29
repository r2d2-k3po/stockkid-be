package net.stockkid.stockkidbe.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
@Component
public class IoUtil {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String naverGrantType;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

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

    public NaverTokenDTO getNaverToken(AuthcodeDTO authcodeDTO) throws Exception {

        WebClient webClient = WebClient.builder()
                .baseUrl(naverTokenUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", naverGrantType)
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("code", authcodeDTO.getAuthcode())
                        .queryParam("state", authcodeDTO.getState())
                        .build())
                .retrieve()
                .bodyToMono(NaverTokenDTO.class)
                .block();
    }

    public NaverUserInfoDTO getNaverUserInfo(String access_token) throws Exception {

        WebClient webClient = WebClient.builder()
                .baseUrl(naverUserInfoUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        NaverProfileDTO naverProfileDTO = webClient.get()
                .header("Authorization", "Bearer " + access_token)
                .retrieve()
                .bodyToMono(NaverProfileDTO.class)
                .block();
        log.info(naverProfileDTO);

        return naverProfileDTO.getResponse();
    }
}
