package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/signup")
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

    @PostMapping("/jwt/member/changePassword")
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
                responseDTO.setApiMsg("Signup OK");
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
}
