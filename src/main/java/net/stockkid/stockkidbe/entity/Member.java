package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    private Long memberId;

    @NotNull
    @NaturalId
    private String username;

    @NotNull
    private @Setter String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private @Setter MemberRole memberRole;

    @NotNull
    @Enumerated(EnumType.STRING)
    private @Setter MemberSocial fromSocial;

    @Column(length = 1024)
    private @Setter String refreshToken;

    @NotNull
    private @Setter boolean accountNonExpired;

    @NotNull
    private @Setter boolean accountNonLocked;

    @NotNull
    private @Setter boolean credentialsNonExpired;

    @NotNull
    private @Setter boolean enabled;

    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @MapsId
    @ToString.Exclude
    private @Setter MemberSettings memberSettings;
}
