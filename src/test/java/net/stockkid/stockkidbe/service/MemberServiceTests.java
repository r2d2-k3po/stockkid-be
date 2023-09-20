package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberServiceTests {

    @Autowired
    private MemberServiceImpl memberService;

    @Test
    public void createUserTest() {
        IntStream.rangeClosed(1,1).forEach(i -> {
            MemberDTO memberDTO = MemberDTO.builder()
                    .username("test1")
                    .password("111111")
                    .memberRole(MemberRole.USER)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .fromSocial(MemberSocial.UP)
                    .build();
            memberService.createUser(memberDTO);
        });
    }

    @Test
    public void loadUserTest() {
        MemberDTO memberDTO = memberService.loadUserByUsername("DefaultSettings@stockkid.net");

        System.out.println(memberDTO.getMemberId());
        System.out.println(memberDTO.getPassword());
        System.out.println(memberDTO.getMemberRole());
        System.out.println(memberDTO.getFromSocial());
        System.out.println(memberDTO.getModDate());
    }

    @Test
    public void deleteUserTest() {
        memberService.deleteUser("test1");
    }
}
