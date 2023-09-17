package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.Member;

public interface MemberService {

    void createUser(MemberDTO dto);

    void updateUser(MemberDTO dto);

    void deleteUser(String username);

    void changePassword(String oldPassword, String newPassword);

    void disableUser(String password);

    void disableSocialUser(Long memberId);

    void updateRefreshToken(String username, String refreshToken);

    boolean userExists(String username);

    MemberDTO loadUserByUsername(String username);

    default Member dtoToEntity(MemberDTO dto) {
        return Member.builder()
                .memberId(dto.getMemberId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .memberRole(dto.getMemberRole())
                .accountNonExpired(dto.isAccountNonExpired())
                .accountNonLocked(dto.isAccountNonLocked())
                .credentialsNonExpired(dto.isCredentialsNonExpired())
                .enabled(dto.isEnabled())
                .fromSocial(dto.getFromSocial())
                .refreshToken(dto.getRefreshToken())
                .build();
    }

    default MemberDTO entityToDto(Member entity) {
        return MemberDTO.builder()
                .memberId(entity.getMemberId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .memberRole(entity.getMemberRole())
                .accountNonExpired(entity.isAccountNonExpired())
                .accountNonLocked(entity.isAccountNonLocked())
                .credentialsNonExpired(entity.isCredentialsNonExpired())
                .enabled(entity.isEnabled())
                .fromSocial(entity.getFromSocial())
                .refreshToken(entity.getRefreshToken())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}
