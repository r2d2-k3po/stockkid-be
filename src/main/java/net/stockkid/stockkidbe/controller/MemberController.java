package net.stockkid.stockkidbe.controller;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @PostMapping({"/member/signup", "/google/member/signin"})
    public ResponseEntity<ResponseDTO> signup(@RequestBody AuthDTO authDTO) {

        log.info("--------------signup--------------");

        Pattern pattern = Pattern.compile("^.{6,30}$");

        Matcher matcherUsername = pattern.matcher(authDTO.getUsername());
        Matcher matcherPassword = pattern.matcher(authDTO.getPassword());

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (matcherUsername.find() && matcherPassword.find()) {
            try {
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setUsername(authDTO.getUsername());
                memberDTO.setPassword(authDTO.getPassword());

                memberService.createUser(memberDTO);
                responseDTO.setApiStatus(ResponseStatus.SIGNUP_OK);
                responseDTO.setApiMsg("Signup OK");
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CREATED);
            } catch (Exception e) {
                log.info("Signup Error : " + e.getMessage());
                responseDTO.setApiStatus(ResponseStatus.SIGNUP_FAIL);
                responseDTO.setApiMsg(e.getMessage());
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
            }
        } else {
            responseDTO.setApiStatus(ResponseStatus.SIGNUP_FAIL);
            responseDTO.setApiMsg("Invalid Username/Password");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/member/googleSignin")
    public ResponseEntity<ResponseDTO> googleSignin(@RequestBody AuthcodeDTO authcodeDTO) {

        log.info("--------------googleSignin--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            log.info("authcode: " + authcodeDTO.getAuthcode());
            log.info("googleClientId: " + googleClientId);

            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), new GsonFactory(),
                    googleClientId, googleClientSecret,
                    authcodeDTO.getAuthcode(),"postmessage")
                    .execute();

            log.info("access " + response.getAccessToken());
            log.info("Id token: " + response.getIdToken());

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(response.getIdToken());

            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                log.info("email : " + email);
                log.info(emailVerified);
            } else {
                throw new Exception("Invalid ID token.");
            }

//            MemberDTO memberDTO = new MemberDTO();
//            memberDTO.setUsername(authDTO.getUsername());
//            memberDTO.setPassword(authDTO.getPassword());
//
//            memberService.createUser(memberDTO);

            responseDTO.setApiStatus(ResponseStatus.LOGIN_OK);
            responseDTO.setApiMsg("Google Sign in OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CREATED);
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                log.info("TokenResponseError: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    log.info(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    log.info(e.getDetails().getErrorUri());
                }
            } else {
                log.info(e.getMessage());
            }
            responseDTO.setApiStatus(ResponseStatus.LOGIN_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.info("Google Sign in Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.LOGIN_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/jwt/member/changePassword")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody PasswordDTO passwordDTO) {

        log.info("--------------changePassword--------------");

        Pattern pattern = Pattern.compile("^.{6,30}$");

        Matcher matcherPassword = pattern.matcher(passwordDTO.getNewPassword());

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (matcherPassword.find()) {
            try {
                memberService.changePassword(passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
                responseDTO.setApiStatus(ResponseStatus.PW_CH_OK);
                responseDTO.setApiMsg("PW_CH OK");
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
            } catch (Exception e) {
                log.info("Password change Error : " + e.getMessage());
                responseDTO.setApiStatus(ResponseStatus.PW_CH_FAIL);
                responseDTO.setApiMsg(e.getMessage());
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.FORBIDDEN);
            }
        } else {
            responseDTO.setApiStatus(ResponseStatus.PW_CH_FAIL);
            responseDTO.setApiMsg("Invalid new Password");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/jwt/member/deleteAccount")
    public ResponseEntity<ResponseDTO> deleteAccount(@RequestBody AuthDTO authDTO) {

        log.info("--------------deleteAccount--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            memberService.disableUser(authDTO.getPassword());
            responseDTO.setApiStatus(ResponseStatus.AC_DL_OK);
            responseDTO.setApiMsg("AC_DL OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Account delete Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.AC_DL_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.FORBIDDEN);
        }
    }
}
