package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JWTClaimsDTO {
    private Long memberId;
    private String username;
    private String role;
    private String social;
}
