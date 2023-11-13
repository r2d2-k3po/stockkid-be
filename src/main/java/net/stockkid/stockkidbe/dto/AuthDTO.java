package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class AuthDTO {
    private String username;
    private String password;
}
