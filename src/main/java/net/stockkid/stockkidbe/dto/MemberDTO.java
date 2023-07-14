package net.stockkid.stockkidbe.dto;

import lombok.*;
import net.stockkid.stockkidbe.entity.MemberRole;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberDTO {

    private Long memberId;
    private String username;
    private String password;
    private MemberRole memberRole;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean fromSocial;
    private LocalDateTime regDate, modDate;
}
