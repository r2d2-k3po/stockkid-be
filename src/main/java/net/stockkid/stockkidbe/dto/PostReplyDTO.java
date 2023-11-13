package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostReplyDTO {

    private Long replyId;
    private Long boardId;
    private Long parentId;
    private String nickname;
    private String content;
}
