package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.dto.TokensDTO;
import net.stockkid.stockkidbe.entity.Member;
import net.stockkid.stockkidbe.entity.MemberInfo;
import net.stockkid.stockkidbe.entity.MemberSettings;
import net.stockkid.stockkidbe.entity.MemberSocial;
import net.stockkid.stockkidbe.repository.MemberRepository;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // permitAll
    @Override
    public Long createUser(MemberDTO dto) {

        if (userExists(dto.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        } else {
            Member member = dtoToEntity(dto);
            member.setPassword(passwordEncoder.encode(dto.getPassword()));

            MemberSettings memberSettings = new MemberSettings();
            memberSettings.setMember(member);

            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setMember(member);

            member.setMemberSettings(memberSettings);
            member.setMemberInfo(memberInfo);
            memberRepository.save(member);

            return member.getId();
        }
    }

    // ADMIN
    @Override
    public void updateUser(MemberDTO dto) {

        Member existingUser;
        if (dto.getId() == null) {
            Optional<Member> optionalUser = memberRepository.findByUsername(dto.getUsername());
            existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            Optional<Member> optionalUser = memberRepository.findById(dto.getId());
            existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));
        }

        existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        existingUser.setMemberRole(dto.getMemberRole());
        existingUser.setAccountNonExpired(dto.isAccountNonExpired());
        existingUser.setAccountNonLocked(dto.isAccountNonLocked());
        existingUser.setCredentialsNonExpired(dto.isCredentialsNonExpired());
        existingUser.setEnabled(dto.isEnabled());
        existingUser.setMemberSocial(dto.getMemberSocial());
        existingUser.setRefreshToken(dto.getRefreshToken());

        memberRepository.save(existingUser);
    }

    // ADMIN
    @Override
    public void deleteUser(Long id) {

        Optional<Member> optionalUser = memberRepository.findById(id);
        Member user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("memberId not found"));

        memberRepository.delete(user);

    }

    //USER
    @Override
    public void changePassword(String oldPassword, String newPassword) {

        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Member> optionalUser = memberRepository.findById(id);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        if (passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(existingUser);
        } else {
            throw new BadCredentialsException("Bad Credentials Exception");
        }
    }

    //USER
    @Override
    public void disableUser(String password) {

        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Member> optionalUser = memberRepository.findById(id);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        if (passwordEncoder.matches(password, existingUser.getPassword())) {
            existingUser.setEnabled(false);
            memberRepository.save(existingUser);
        } else {
            throw new BadCredentialsException("Bad Credentials Exception");
        }
    }

    @Override
    public void disableSocialUser(Long id) {
        Optional<Member> optionalUser = memberRepository.findById(id);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        existingUser.setEnabled(false);
        memberRepository.save(existingUser);
    }

    @Override
    public boolean userExists(String username) {

        return memberRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        log.info("UserDetailsService loadUserByUsername " + username);

        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member member = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (member.getMemberSocial() != MemberSocial.UP) throw new UsernameNotFoundException("User not found");

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

    @Override
    public MemberDTO findUserByUsername(String username) {

        Optional<Member> result = memberRepository.findByUsername(username);
        return result.map(this::entityToDto).orElse(null);
    }

    public TokensDTO generateTokens(Long sid, String sub, String rol, String soc) throws Exception {

        Member existingUser;
        if (sid == null) {
            Optional<Member> optionalUser = memberRepository.findByUsername(sub);
            existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            Optional<Member> optionalUser = memberRepository.findById(sid);
            existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));
        }

        return generateTokensDTO(existingUser, rol, soc);
    }

    public TokensDTO rotateTokens(Long sid, String rol, String soc, String refreshToken) throws Exception {

        Optional<Member> optionalUser = memberRepository.findById(sid);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        if (Objects.equals(refreshToken, existingUser.getRefreshToken())) {
            return generateTokensDTO(existingUser, rol, soc);
        } else {
            throw new Exception("refreshToken not valid");
        }
    }

    private TokensDTO generateTokensDTO(Member existingUser, String rol, String soc) throws Exception {

        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setAccessToken(jwtUtil.generateAccessToken(existingUser.getId(), rol));
        tokensDTO.setRefreshToken(jwtUtil.generateRefreshToken(existingUser.getId(), existingUser.getUsername(), rol, soc));

        log.info("successful accessToken : " + tokensDTO.getAccessToken());
        log.info("successful refreshToken : " + tokensDTO.getRefreshToken());

        existingUser.setRefreshToken(tokensDTO.getRefreshToken());
        memberRepository.save(existingUser);

        return tokensDTO;
    }

    public void invalidateToken(Long sid, String refreshToken) throws Exception {

        Optional<Member> optionalUser = memberRepository.findById(sid);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        if (Objects.equals(refreshToken, existingUser.getRefreshToken())) {
            existingUser.setRefreshToken(null);
            memberRepository.save(existingUser);
        } else {
            throw new Exception("refreshToken not valid");
        }
    }

// STAFF

//    public List<UserDetails> loadAllUsers() {
//        // Implement the logic to load all users from the repository and convert them to UserDetails objects
//        // Return the list of UserDetails
//        return null;
}
