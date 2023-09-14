package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private @Setter String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private @Setter MemberRole memberRole;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private @Setter MemberSocial fromSocial;

    @Column(length = 1024)
    private @Setter String refreshToken;

    @Column(nullable = false)
    private @Setter boolean accountNonExpired;

    @Column(nullable = false)
    private @Setter boolean accountNonLocked;

    @Column(nullable = false)
    private @Setter boolean credentialsNonExpired;

    @Column(nullable = false)
    private @Setter boolean enabled;
}
