package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfo {

    @Id
    private Long memberId;

    @NotNull
    private @Setter Long likeCount = 0L;

    @NotNull
    private @Setter Long credit = 0L;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberInfo")
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Board> boardList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberInfo")
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Reply> replyList = new ArrayList<>();
}
