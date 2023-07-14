package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.Member;
import net.stockkid.stockkidbe.repository.MemberRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(MemberDTO dto) {

        log.info("DTO -------------------------");
        log.info(dto);

        Member entity = dtoToEntity(dto);

        log.info(entity);

        memberRepository.save(entity);
    }

    @Override
    public void updateUser(MemberDTO dto) {
        Optional<Member> optionalUser = memberRepository.findByUsername(dto.getUsername());
        Member existingUser = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        existingUser.setMemberRole(dto.getMemberRole());
        existingUser.setAccountNonExpired(dto.isAccountNonExpired());
        existingUser.setAccountNonLocked(dto.isAccountNonLocked());
        existingUser.setCredentialsNonExpired(dto.isCredentialsNonExpired());
        existingUser.setEnabled(dto.isEnabled());

        memberRepository.save(existingUser);

    }

    @Override
    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // Get the authenticated user from the security context or session
        // Update the password for the authenticated user
        // Save the changes using userRepository.save(user)

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }


//    public List<UserDetails> loadAllUsers() {
//        // Implement the logic to load all users from the repository and convert them to UserDetails objects
//        // Return the list of UserDetails
//        return null;
}
