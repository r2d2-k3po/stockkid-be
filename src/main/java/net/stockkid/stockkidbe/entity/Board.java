package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static org.hibernate.Length.LONG32;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "memberId"),
        @Index(columnList = "rootId"),
        @Index(columnList = "tag1"),
        @Index(columnList = "tag2"),
        @Index(columnList = "tag3")
})
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @NotNull
    private @Setter Long memberId;

    @Column
    private @Setter Long rootId;

    @Column
    private @Setter Long parentId;

    @Column
    @Enumerated(EnumType.STRING)
    private @Setter BoardCategory boardCategory;

    @NotNull
    private @Setter String nickname;

    @Column
    private @Setter String title;

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

    @Column
    private @Setter int readCount;

    @Column
    private @Setter int replyCount;

    @NotNull
    private @Setter int likeCount;
}
