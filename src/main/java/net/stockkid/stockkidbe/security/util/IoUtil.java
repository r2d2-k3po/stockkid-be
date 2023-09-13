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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.redirect-uri}")
    private String kakaoRedirectUri;

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

    public KakaoTokenDTO getKakaoToken(String authcode) throws Exception {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", kakaoGrantType);
        formData.add("client_id", kakaoClientId);
        formData.add("redirect_uri", kakaoRedirectUri);
        formData.add("code", authcode);
        formData.add("client_secret", kakaoClientSecret);

        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoTokenUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        return webClient.post()
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(KakaoTokenDTO.class)
                .block();
    }
}
