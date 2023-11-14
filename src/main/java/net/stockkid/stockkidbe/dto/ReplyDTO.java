package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.stockkid.stockkidbe.entity.BoardCategory;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplyDTO {

    private Long replyId;
    private Long parentId;
    private Long memberId;
    private String nickname;
    private String content;
    private int likeCount;
    private LocalDateTime regDate, modDate;
}
