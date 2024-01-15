package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.Reply;

import java.nio.charset.StandardCharsets;

public interface BoardService {

    IdDTO register(SaveBoardDTO dto);

    void modify(SaveBoardDTO dto);

    void delete(Long boardId);

    BoardPageDTO readPage(int page, int size, String boardCategory, String sortBy);

    BoardReplyDTO read(Long boardId);

    void like(LikeDTO likeDTO);

    BoardPageDTO searchPage(int page, int size, String boardCategory, String sortBy, String tag);

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
        boardDTO.setContent(new String(entity.getContent(), StandardCharsets.UTF_8));

        return boardDTO;
    }

    default ReplyDTO replyToReplyDto(Reply reply){
        return ReplyDTO.builder()
                .replyId(reply.getId())
                .parentId(reply.getParentId())
                .memberId(reply.getMemberInfo().getMemberId())
                .nickname(reply.getNickname())
                .content(new String(reply.getContent(), StandardCharsets.UTF_8))
                .likeCount(reply.getLikeCount())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }
}
