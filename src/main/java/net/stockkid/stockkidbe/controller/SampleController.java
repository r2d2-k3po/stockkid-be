package net.stockkid.stockkidbe.controller;

import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.security.dto.MemberDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/sample/")
public class SampleController {

    @GetMapping("/all")
    public String exAll() {
        return "exAll.....";
    }

    @GetMapping("/member")
    public String exMember() {
        return "exMember.....";
    }

    @GetMapping("/staff")
    public Object exStaff(@AuthenticationPrincipal MemberDTO memberDTO) {
        return memberDTO;
    }

    @GetMapping("/admin")
    public String exAdmin() {
        return "exAdmin.....";
    }
}
