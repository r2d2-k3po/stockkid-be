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
    private @Setter int likeCount = 0;

    @NotNull
    private @Setter int credit = 0;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter Member member;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "memberInfo", orphanRemoval = true)
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Board> boardList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "memberInfo", orphanRemoval = true)
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Reply> replyList = new ArrayList<>();

    public void addLikeCount(int number) {
        likeCount += number;
    }

    public void addCredit(int number) {
        credit += number;
    }
}
