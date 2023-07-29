package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
}
