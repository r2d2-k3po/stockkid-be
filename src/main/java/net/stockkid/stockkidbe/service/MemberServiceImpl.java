package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.Member;
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
    public void createUser(MemberDTO dto) throws IllegalArgumentException {

        log.info("DTO -------------------------");
        log.info(dto);

        if (userExists(dto.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        } else {
            Member entity = dtoToEntity(dto);
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
            log.info(entity);
            memberRepository.save(entity);
        }
    }

    // ADMIN
    @Override
    public void updateUser(MemberDTO dto) throws IllegalArgumentException, UsernameNotFoundException {

        Member existingUser;
        if (dto.getMemberId() == null) {
            Optional<Member> optionalUser = memberRepository.findByUsername(dto.getUsername());
            existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            Optional<Member> optionalUser = memberRepository.findById(dto.getMemberId());
            existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));
        }

        existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        existingUser.setMemberRole(dto.getMemberRole());
        existingUser.setAccountNonExpired(dto.isAccountNonExpired());
        existingUser.setAccountNonLocked(dto.isAccountNonLocked());
        existingUser.setCredentialsNonExpired(dto.isCredentialsNonExpired());
        existingUser.setEnabled(dto.isEnabled());
        existingUser.setFromSocial(dto.getFromSocial());

        memberRepository.save(existingUser);
    }

    // ADMIN
    @Override
    public void deleteUser(String username) throws UsernameNotFoundException {

        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        memberRepository.delete(user);

    }

    //USER
    @Override
    public void changePassword(String oldPassword, String newPassword) throws UsernameNotFoundException, BadCredentialsException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(existingUser);
        } else {
            throw new BadCredentialsException("Bad Credentials Exception");
        }
    }

    //USER
    @Override
    public void disableUser(String password) throws UsernameNotFoundException, BadCredentialsException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        Member existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, existingUser.getPassword())) {
            existingUser.setEnabled(false);
            memberRepository.save(existingUser);
        } else {
            throw new BadCredentialsException("Bad Credentials Exception");
        }
    }

    @Override
    public void disableSocialUser(Long memberId) throws IllegalArgumentException {
        Optional<Member> optionalUser = memberRepository.findById(memberId);
        Member existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        existingUser.setEnabled(false);
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
