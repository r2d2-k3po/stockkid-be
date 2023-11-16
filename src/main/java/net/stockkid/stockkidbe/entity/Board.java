package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.Length.LONG;

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

    @Column
    @Enumerated(EnumType.STRING)
    private @Setter BoardCategory boardCategory;

    @NotNull
    private @Setter String nickname;

    @Column
    private @Setter String title;

    @NotNull
    @ToString.Exclude
    private @Setter String preview;

    @Column(length = LONG)
    @NotNull
    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    private @Setter String content;

    @Column
    private @Setter String tag1;

    @Column
    private @Setter String tag2;

    @Column
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
