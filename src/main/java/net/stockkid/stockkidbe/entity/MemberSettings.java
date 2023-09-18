package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSettings {

    @Id
    private Long memberId;

    @Column
    private @Setter String screenTitle1;

    @Lob
    @Column
    private @Setter String screenSetting1;

    @Column
    private @Setter String screenTitle2;

    @Lob
    @Column
    private @Setter String screenSetting2;

    @Column
    private @Setter String screenTitle3;

    @Lob
    @Column
    private @Setter String screenSetting3;
}
