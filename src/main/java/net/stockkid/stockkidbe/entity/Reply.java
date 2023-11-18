package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static org.hibernate.Length.LONG;

@Entity
@ToString
@Getter
@NoArgsConstructor
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    private @Setter Long parentId;

    @NotNull
    private @Setter String nickname;

    @Column(length = LONG)
    @NotNull
    @ToString.Exclude
    private @Setter String content;

    @NotNull
    private @Setter int likeCount = 0;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private @Setter MemberInfo memberInfo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @ToString.Exclude
    private @Setter Board board;

    public void addLikeCount(int number) {
        likeCount += number;
    }
}
