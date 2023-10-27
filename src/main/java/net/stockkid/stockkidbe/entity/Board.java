package net.stockkid.stockkidbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @NotNull
    private @Setter Long memberId;

    @Column
    private @Setter Long rootId;

    @Column
    private @Setter Long parentId;

    @NotNull
    private @Setter String nickname;

    @NotNull
    private @Setter String title;

    @Lob
    @Column
    @ToString.Exclude
    private @Setter String content;

    @Column
    private @Setter int replyCount;

}
