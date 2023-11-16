package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@ToString
@Getter
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

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "memberInfo")
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Board> boardList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "memberInfo")
    @OrderBy("id desc")
    @ToString.Exclude
    private @Setter List<Reply> replyList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "member_like_board", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "board_id"))
    @ToString.Exclude
    private @Setter Set<Board> likeBoardSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "member_like_reply", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "reply_id"))
    @ToString.Exclude
    private @Setter Set<Reply> likeReplySet = new HashSet<>();

    public void addLikeCount(int number) {
        likeCount += number;
    }

    public void addCredit(int number) {
        credit += number;
    }
}
