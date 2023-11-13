package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NaverProfileDTO {
    private String resultcode;
    private String message;
    private NaverUserInfoDTO response;
}
