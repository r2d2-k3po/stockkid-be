package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api/member/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberDTO memberDTO) {

        log.info("--------------signup--------------");

        try {
            memberService.createUser(memberDTO);
            return new ResponseEntity<>("Signup OK", HttpStatus.OK);
        } catch (Exception e) {
            log.info("Signup Error : " + e.getMessage());
            return new ResponseEntity<>("Signup Error : " + e.getMessage(), HttpStatus.OK);
        }


    }
}
