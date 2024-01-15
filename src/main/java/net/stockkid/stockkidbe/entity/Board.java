package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "tag1"),
        @Index(columnList = "tag2"),
        @Index(columnList = "tag3")
})
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private @Setter BoardCategory boardCategory;

    @NotNull
    private @Setter String nickname;

    private @Setter String title;

    @Column(length = 1024)
    @NotNull
    @ToString.Exclude
    private @Setter String preview;

    @Column(length = 2000000)
    @NotNull
    @ToString.Exclude
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private @Setter byte[] content;

    private @Setter String tag1;

    private @Setter String tag2;

    private @Setter String tag3;

    @NotNull
    private @Setter int readCount = 0;

    @NotNull
    private @Setter int replyCount = 0;

    @NotNull
    private @Setter int likeCount = 0;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter MemberInfo memberInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    @OrderBy("id asc")
    @ToString.Exclude
    private @Setter List<Reply> replyList = new ArrayList<>();

    public void addReadCount() {
        readCount += 1;
    }

    public void addReplyCount() {
        replyCount += 1;
    }

    public void addLikeCount(int number) {
        likeCount += number;
    }
}
