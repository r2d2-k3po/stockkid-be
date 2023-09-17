package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JWTClaimsDTO {
    private Long memberId;
    private String username;
    private String role;
    private String social;
}
