package net.stockkid.stockkidbe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BoardPageDTO {

    private List<BoardDTO> boardDTOList;
    private int totalPages;
}
