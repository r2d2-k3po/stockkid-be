package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NaverProfileDTO {
    private String resultcode;
    private String message;
    private NaverUserInfoDTO response;
}
