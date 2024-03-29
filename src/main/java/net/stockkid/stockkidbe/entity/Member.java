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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

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
    private @Setter MemberSocial memberSocial;

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

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, optional = false, fetch = FetchType.LAZY, mappedBy = "member")
    @ToString.Exclude
    private @Setter MemberSettings memberSettings;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, optional = false, fetch = FetchType.LAZY, mappedBy = "member")
    @ToString.Exclude
    private @Setter MemberInfo memberInfo;
}
