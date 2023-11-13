package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class KakaoTokenDTO {
    private String access_token;
    private String refresh_token;
    private String id_token;
    private String token_type;
    private Integer expires_in;
    private Integer refresh_token_expires_in;
    private String scope;
}
