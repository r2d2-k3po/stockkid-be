package net.stockkid.stockkidbe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;
import net.stockkid.stockkidbe.security.util.TokenUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Collections;

@Log4j2
public class ApiGoogleFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtil tokenUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    private final AntPathRequestMatcher antPathRequestMatcher;

    private final MemberService memberService;

    public ApiGoogleFilter(AntPathRequestMatcher antPathRequestMatcher, MemberService memberService) {
        this.antPathRequestMatcher = antPathRequestMatcher;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiGoogleFilter---------------------");

            try {
                StringBuilder requestBody = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
                AuthcodeDTO authcodeDTO = new ObjectMapper().readValue(requestBody.toString(), AuthcodeDTO.class);

                log.info("authcode: " + authcodeDTO.getAuthcode());

                GoogleTokenResponse googleTokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(), new GsonFactory(),
                        googleClientId, googleClientSecret,
                        authcodeDTO.getAuthcode(), "postmessage")
                        .execute();

                log.info("Id token: " + googleTokenResponse.getIdToken());

                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(googleClientId))
                        .build();

                GoogleIdToken idToken = verifier.verify(googleTokenResponse.getIdToken());
                if (idToken == null) {
                    throw new Exception("Invalid ID token.");
                }

                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                log.info("email : " + email);
                log.info(emailVerified);
                if (!emailVerified) {
                    throw new Exception("Email not verified.");
                }

                response.setContentType("application/json;charset=utf-8");

                MemberDTO memberDTO = memberService.loadUserByUsername(email);

                if (memberDTO == null) {
                    TokensDTO tokensDTO = tokenUtil.generateTokens(email, MemberRole.USER.name(), MemberSocial.GGL.name());

                    MemberDTO newMemberDTO = new MemberDTO();
                    newMemberDTO.setUsername(email);
                    newMemberDTO.setPassword(generateRandomPassword(30));
                    newMemberDTO.setFromSocial(MemberSocial.GGL);

                    memberService.createUser(newMemberDTO);

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);
                    String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);
                    PrintWriter writer = response.getWriter();
                    writer.print(jsonBody);
                    return;
                } else if (memberDTO.getFromSocial() == MemberSocial.GGL) {
                    if (!memberDTO.isEnabled()) throw new Exception("User disabled");
                    if (!memberDTO.isAccountNonExpired()) throw new Exception("Account expired");
                    if (!memberDTO.isCredentialsNonExpired()) throw new Exception("Credential expired");
                    if (!memberDTO.isAccountNonLocked()) throw new Exception("Account locked");

                    if (new AntPathRequestMatcher("/api/google/member/signin").matches(request)) {
                        TokensDTO tokensDTO = tokenUtil.generateTokens(email, memberDTO.getMemberRole().name(), MemberSocial.GGL.name());

                        response.setStatus(HttpServletResponse.SC_OK);
                        ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);
                        String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);
                        PrintWriter writer = response.getWriter();
                        writer.print(jsonBody);
                    } else if (new AntPathRequestMatcher("/api/google/member/deleteAccount").matches(request)) {
                        memberDTO.setEnabled(false);
                        memberService.disableSocialUser(memberDTO.getMemberId());

                        ResponseDTO responseDTO = new ResponseDTO();
                        responseDTO.setApiStatus(ResponseStatus.AC_DL_OK);
                        responseDTO.setApiMsg("AC_DL OK");
                        String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);
                        PrintWriter writer = response.getWriter();
                        writer.print(jsonBody);
                    }
                    return;
                } else {
                    throw new Exception("User already exists");
                }
            } catch (Exception error) {
                log.info(error.getMessage());

                response.setStatus(HttpServletResponse.SC_CONFLICT);

                ResponseDTO responseDTO = new ResponseDTO();
                if (new AntPathRequestMatcher("/api/google/member/signin").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.LOGIN_FAIL);
                } else if (new AntPathRequestMatcher("/api/google/member/deleteAccount").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.AC_DL_FAIL);
                }
                responseDTO.setApiMsg(error.getMessage());

                String jsonBody = new ObjectMapper().writeValueAsString(responseDTO);

                PrintWriter writer = response.getWriter();
                writer.print(jsonBody);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String generateRandomPassword(int length) {

        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }
        return password.toString();
    }
}
