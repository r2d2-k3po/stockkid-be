package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Member;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void memberRoleTest() {
        String hierarchy = MemberRole.getRoleHierarchy();
        System.out.println("memberRoleTest : ");
        System.out.println(hierarchy);
    }

//    @Test
//    public void insertMember() {
//        IntStream.rangeClosed(1,20).forEach(i -> {
//            Member member = Member.builder()
//                    .username("user"+i)
//                    .password(passwordEncoder.encode("user"+i))
//                    .memberRole(MemberRole.USER)
//                    .accountNonExpired(true)
//                    .accountNonLocked(true)
//                    .credentialsNonExpired(true)
//                    .enabled(true)
//                    .memberSocial(MemberSocial.UP)
//                    .build();
//            memberRepository.save(member);
//        });
//    }

//    @Test
//    public void testRead() {
//        Optional<Member> result = memberRepository.findById(1L);
//
//        Member member = result.get();
//        System.out.println(member);
//        System.out.println(member.getMemberSettings());
////        System.out.println(member.getMemberSettings());
//    }
}
