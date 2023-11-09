package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.Member;
import net.stockkid.stockkidbe.entity.MemberInfo;
import net.stockkid.stockkidbe.entity.MemberSettings;
import net.stockkid.stockkidbe.repository.MemberRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // permitAll
    @Override
    public void createUser(MemberDTO dto) {

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
    public void deleteUser(String username) {

        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
    public void updateRefreshToken(String username, String refreshToken) {
        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setRefreshToken(refreshToken);
        memberRepository.save(existingUser);
    }

    @Override
    public boolean userExists(String username) {

        return memberRepository.existsByUsername(username);
    }

    @Override
    public MemberDTO loadUserByUsername(String username) {

        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isEmpty()) {
            return null;
        }
        Member entity = result.get();
        return entityToDto(entity);
    }

// STAFF

//    public List<UserDetails> loadAllUsers() {
//        // Implement the logic to load all users from the repository and convert them to UserDetails objects
//        // Return the list of UserDetails
//        return null;
}
