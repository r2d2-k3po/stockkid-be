package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.BoardDTO;
import net.stockkid.stockkidbe.dto.BoardPageDTO;
import net.stockkid.stockkidbe.dto.PostBoardDTO;
import net.stockkid.stockkidbe.entity.Board;

public interface BoardService {

    void register(PostBoardDTO dto);

    void modify(PostBoardDTO dto);

    void delete(Long boardId);

    BoardPageDTO readPage(int page, int size, String boardCategory, String sortBy);

    default BoardDTO entityToDto(Board entity) {
        return BoardDTO.builder()
                .boardId(entity.getId())
                .memberId(entity.getMemberId())
                .boardCategory(entity.getBoardCategory())
                .nickname(entity.getNickname())
                .title(entity.getTitle())
                .content(entity.getContent())
                .tag1(entity.getTag1())
                .tag2(entity.getTag2())
                .tag3(entity.getTag3())
                .readCount(entity.getReadCount())
                .replyCount(entity.getReplyCount())
                .likeCount(entity.getLikeCount())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }

}
