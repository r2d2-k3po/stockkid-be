package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthcodeDTO {
    private String authcode;
    private String state;
    private String nonce;
}
