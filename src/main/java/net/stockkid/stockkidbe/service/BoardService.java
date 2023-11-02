package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.BoardDTO;
import net.stockkid.stockkidbe.dto.PostDTO;
import net.stockkid.stockkidbe.dto.ReplyDTO;
import net.stockkid.stockkidbe.entity.Board;

public interface BoardService {

    void registerPost(PostDTO dto);

    void modifyPost(PostDTO dto);

    void registerReply(ReplyDTO dto);

    void modifyReply(ReplyDTO dto);

    default Board dtoToEntity(BoardDTO dto) {
        return Board.builder()
                .boardId(dto.getBoardId())
                .memberId(dto.getMemberId())
                .rootId(dto.getRootId())
                .parentId(dto.getParentId())
                .boardCategory(dto.getBoardCategory())
                .nickname(dto.getNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .tag1(dto.getTag1())
                .tag2(dto.getTag2())
                .tag3(dto.getTag3())
                .readCount(dto.getReadCount())
                .replyCount(dto.getReplyCount())
                .likeCount(dto.getLikeCount())
                .build();
    }

    default BoardDTO entityToDto(Board entity) {
        return BoardDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(entity.getMemberId())
                .rootId(entity.getRootId())
                .parentId(entity.getParentId())
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