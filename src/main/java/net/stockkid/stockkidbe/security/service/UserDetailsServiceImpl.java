package net.stockkid.stockkidbe.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.entity.Member;
import net.stockkid.stockkidbe.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("MemberDetailsService loadUserByUsername " + username);

        Optional<Member> result = memberRepository.findByUsername(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Member member = result.get();

        log.info("----------------------------------------------");
        log.info(member);

        return new User(
                member.getUsername(),
                member.getPassword(),
                member.isEnabled(),
                member.isAccountNonExpired(),
                member.isCredentialsNonExpired(),
                member.isAccountNonLocked(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole().name()))
        );
    }
}
