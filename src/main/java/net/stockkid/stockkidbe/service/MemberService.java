package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.entity.Member;

public interface MemberService {

    void createUser(MemberDTO dto);

    void updateUser(MemberDTO dto);

    void deleteUser(String username);

    void changePassword(String oldPassword, String newPassword);

    boolean userExists(String username);



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
                .fromSocial(dto.isFromSocial())
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
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}
