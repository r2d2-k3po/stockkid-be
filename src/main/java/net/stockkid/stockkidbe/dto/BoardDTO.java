package net.stockkid.stockkidbe.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardDTO {

    private Long boardId;
    private Long memberId;
    private Long rootId;
    private Long parentId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime regDate, modDate;
}
