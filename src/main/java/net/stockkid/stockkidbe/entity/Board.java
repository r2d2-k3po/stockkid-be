package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.Length.LONG32;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
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

    @Column(length = LONG32)
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
    private @Setter Long readCount = 0L;

    @NotNull
    private @Setter Long replyCount = 0L;

    @NotNull
    private @Setter Long likeCount = 0L;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter MemberInfo memberInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    @OrderBy("id asc")
    @ToString.Exclude
    private @Setter List<Reply> replyList = new ArrayList<>();
}
