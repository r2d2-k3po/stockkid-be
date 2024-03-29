package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SaveBoardDTO {

    private Long boardId;
    private String boardCategory;
    private String nickname;
    private String title;
    private String preview;
    private String content;
    private String tag1, tag2, tag3;
}
