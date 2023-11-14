package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BoardReplyDTO {

    private BoardDTO boardDTO;
    private List<ReplyDTO> replyDTOList;
}
