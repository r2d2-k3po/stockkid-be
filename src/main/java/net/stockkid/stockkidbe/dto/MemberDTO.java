package net.stockkid.stockkidbe.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberDTO extends AuthDTO {

    private Long id;
    private MemberRole memberRole = MemberRole.USER;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private MemberSocial memberSocial = MemberSocial.UP;
    private String refreshToken;
    private LocalDateTime regDate, modDate;
}
