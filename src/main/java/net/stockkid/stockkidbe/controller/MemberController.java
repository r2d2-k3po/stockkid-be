package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import net.stockkid.stockkidbe.dto.ResponseStatus;
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
@RequestMapping("/api/member/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody MemberDTO memberDTO) {

        log.info("--------------signup--------------");

        Pattern pattern = Pattern.compile("^.{6,30}$");
        Matcher matcherUsername = pattern.matcher(memberDTO.getUsername());
        Matcher matcherPassword = pattern.matcher(memberDTO.getPassword());

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (matcherUsername.find() && matcherPassword.find()) {
            try {
                memberService.createUser(memberDTO);
                responseDTO.setResponseStatus(ResponseStatus.SIGNUP_OK);
                responseDTO.setResponseMessage("Signup OK");
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CREATED);
            } catch (Exception e) {
                log.info("Signup Error : " + e.getMessage());
                responseDTO.setResponseStatus(ResponseStatus.SIGNUP_FAIL);
                responseDTO.setResponseMessage(e.getMessage());
                return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.ACCEPTED);
            }
        } else {
            responseDTO.setResponseStatus(ResponseStatus.SIGNUP_FAIL);
            responseDTO.setResponseMessage("Check Username/Password");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.ACCEPTED);
        }




    }
}
