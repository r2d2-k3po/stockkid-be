package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private @Setter String screenTitle1;

    @Lob
    @Column
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting1;

    @Column
    private @Setter String screenTitle2;

    @Lob
    @Column
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting2;

    @Column
    private @Setter String screenTitle3;

    @Lob
    @Column
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting3;
}
