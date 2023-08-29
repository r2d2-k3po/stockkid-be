package net.stockkid.stockkidbe.security.filter;

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
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.TokenUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Log4j2
public class ApiNaverFilter  extends OncePerRequestFilter {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private IoUtil ioUtil;



    private final AntPathRequestMatcher antPathRequestMatcher;

    private final MemberService memberService;

    public ApiNaverFilter(AntPathRequestMatcher antPathRequestMatcher, MemberService memberService) {
        this.antPathRequestMatcher = antPathRequestMatcher;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiNaverFilter---------------------");

            try {
                AuthcodeDTO authcodeDTO = ioUtil.readRequestBody(request, AuthcodeDTO.class);

                log.info("authcode: " + authcodeDTO.getAuthcode());
                log.info("state: " + authcodeDTO.getState());

                NaverTokenDTO naverTokenDTO = ioUtil.getNaverToken(authcodeDTO);

                log.info(naverTokenDTO);

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

                MemberDTO memberDTO = memberService.loadUserByUsername(email);

                if (memberDTO == null) {
                    MemberDTO newMemberDTO = new MemberDTO();
                    newMemberDTO.setUsername(email);
                    newMemberDTO.setPassword(tokenUtil.generateRandomPassword(30));
                    newMemberDTO.setFromSocial(MemberSocial.GGL);

                    log.info("create new Google user");
                    memberService.createUser(newMemberDTO);

                    log.info("create tokens for new user");
                    TokensDTO tokensDTO = tokenUtil.generateTokens(email, MemberRole.USER.name(), MemberSocial.GGL.name());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                    ioUtil.writeResponseBody(response, responseDTO);
                    return;
                } else if (memberDTO.getFromSocial() == MemberSocial.GGL) {
                    if (!memberDTO.isEnabled()) throw new Exception("User disabled");
                    if (!memberDTO.isAccountNonExpired()) throw new Exception("Account expired");
                    if (!memberDTO.isCredentialsNonExpired()) throw new Exception("Credential expired");
                    if (!memberDTO.isAccountNonLocked()) throw new Exception("Account locked");

                    if (new AntPathRequestMatcher("/api/google/member/signin").matches(request)) {
                        log.info("create tokens for existing user");
                        TokensDTO tokensDTO = tokenUtil.generateTokens(email, memberDTO.getMemberRole().name(), MemberSocial.GGL.name());

                        response.setStatus(HttpServletResponse.SC_OK);
                        ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                        ioUtil.writeResponseBody(response, responseDTO);
                    } else if (new AntPathRequestMatcher("/api/google/member/deleteAccount").matches(request)) {
                        log.info("disable existing Google user");
                        memberDTO.setEnabled(false);
                        memberService.disableSocialUser(memberDTO.getMemberId());

                        response.setStatus(HttpServletResponse.SC_OK);

                        ResponseDTO responseDTO = new ResponseDTO();
                        responseDTO.setApiStatus(ResponseStatus.AC_DL_OK);
                        responseDTO.setApiMsg("AC_DL OK");

                        ioUtil.writeResponseBody(response, responseDTO);
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

                ioUtil.writeResponseBody(response, responseDTO);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}