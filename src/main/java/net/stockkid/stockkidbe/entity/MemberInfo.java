package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfo {

    @Id
    private Long memberId;

    @Column
    private @Setter Long likeCount;

    @Column
    private @Setter Long credit;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter Member member;
}
