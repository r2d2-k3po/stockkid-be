package net.stockkid.stockkidbe.dto;

import lombok.*;
import net.stockkid.stockkidbe.entity.BoardCategory;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardDTO {

    private Long boardId;
    private Long memberId;
    private BoardCategory boardCategory;
    private String nickname;
    private String title;
    private String preview;
    private String content;
    private String tag1, tag2, tag3;
    private int readCount, replyCount, likeCount;
    private LocalDateTime regDate, modDate;
}
