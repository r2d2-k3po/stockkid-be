package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDTO {

    private Long boardId;
    private String boardCategory;
    private String nickname;
    private String title;
    private String content;
    private String tag1, tag2, tag3;
}
