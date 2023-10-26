package net.stockkid.stockkidbe.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardDTO {

    private Long boardId;
    private Long memberId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime regDate, modDate;
}
