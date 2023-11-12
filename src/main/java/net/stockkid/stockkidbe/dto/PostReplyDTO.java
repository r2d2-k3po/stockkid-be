package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostReplyDTO {

    private Long replyId;
    private Long boardId;
    private Long parentId;
    private String nickname;
    private String content;
}
