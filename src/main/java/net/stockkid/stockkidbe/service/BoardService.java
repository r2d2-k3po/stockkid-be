package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.Reply;

public interface BoardService {

    void register(PostBoardDTO dto);

    void modify(PostBoardDTO dto);

    void delete(Long boardId);

    BoardPageDTO readPage(int page, int size, String boardCategory, String sortBy);

    BoardReplyDTO read(Long boardId);

    default BoardDTO entityToPreviewDto(Board entity) {
        return BoardDTO.builder()
                .boardId(entity.getId())
                .memberId(entity.getMemberInfo().getMemberId())
                .boardCategory(entity.getBoardCategory())
                .nickname(entity.getNickname())
                .title(entity.getTitle())
                .preview(entity.getPreview())
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

    default BoardDTO entityToDto(Board entity) {
        BoardDTO boardDTO = entityToPreviewDto(entity);
        boardDTO.setContent(entity.getContent());

        return boardDTO;
    }

    default ReplyDTO replyToReplyDto(Reply reply){
        return ReplyDTO.builder()
                .replyId(reply.getId())
                .parentId(reply.getParentId())
                .memberId(reply.getMemberInfo().getMemberId())
                .nickname(reply.getNickname())
                .content(reply.getContent())
                .likeCount(reply.getLikeCount())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }
}
