package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.dto.TokensDTO;
import net.stockkid.stockkidbe.entity.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    Long createUser(MemberDTO dto);

    void updateUser(MemberDTO dto);

    void deleteUser(Long id);

    void changePassword(String oldPassword, String newPassword);

    void disableUser(String password);

    void disableSocialUser(Long id);

    boolean userExists(String username);

    MemberDTO findUserByUsername(String username);

    TokensDTO generateTokens(Long sid, String sub, String rol, String soc) throws Exception;

    TokensDTO rotateTokens(Long sid, String rol, String soc, String refreshToken) throws Exception;

    void invalidateToken(Long sid, String refreshToken) throws Exception;

    default Member dtoToEntity(MemberDTO dto) {
        return Member.builder()
                .username(dto.getUsername())
                .memberRole(dto.getMemberRole())
                .accountNonExpired(dto.isAccountNonExpired())
                .accountNonLocked(dto.isAccountNonLocked())
                .credentialsNonExpired(dto.isCredentialsNonExpired())
                .enabled(dto.isEnabled())
                .memberSocial(dto.getMemberSocial())
                .refreshToken(dto.getRefreshToken())
                .build();
    }

    default MemberDTO entityToDto(Member entity) {
        return MemberDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .memberRole(entity.getMemberRole())
                .accountNonExpired(entity.isAccountNonExpired())
                .accountNonLocked(entity.isAccountNonLocked())
                .credentialsNonExpired(entity.isCredentialsNonExpired())
                .enabled(entity.isEnabled())
                .memberSocial(entity.getMemberSocial())
                .refreshToken(entity.getRefreshToken())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}
