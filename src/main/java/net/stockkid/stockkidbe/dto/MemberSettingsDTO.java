package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberSettingsDTO {

    private Long memberId;
    private String screenTitle1;
    private String screenSetting1;
    private String screenTitle2;
    private String screenSetting2;
    private String screenTitle3;
    private String screenSetting3;
}
