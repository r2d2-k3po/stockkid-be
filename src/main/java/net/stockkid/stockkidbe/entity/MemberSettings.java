package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import lombok.*;

import static org.hibernate.Length.LONG;

@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSettings {

    @Id
    private Long memberId;

    @Column
    private @Setter String screenTitle1;

    @Column(length = LONG)
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting1;

    @Column
    private @Setter String screenTitle2;

    @Column(length = LONG)
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting2;

    @Column
    private @Setter String screenTitle3;

    @Column(length = LONG)
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String screenSetting3;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter Member member;
}
