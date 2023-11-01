package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.PostDTO;
import net.stockkid.stockkidbe.dto.ReplyDTO;
import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.BoardCategory;
import net.stockkid.stockkidbe.repository.BoardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    // USER
    @Override
    public void registerPost(PostDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Board board = Board.builder()
                .memberId(memberId)
                .boardCategory(BoardCategory.valueOf(dto.getBoardCategory()))
                .nickname(dto.getNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .tag1(dto.getTag1())
                .tag2(dto.getTag2())
                .tag3(dto.getTag3())
                .readCount(0)
                .replyCount(0)
                .likeCount(0)
                .build();

        boardRepository.save(board);
    }

    @Override
    public void registerReply(ReplyDTO dto) {

        Optional<Board> optionalPost = boardRepository.findById(dto.getRootId());
        Board existingPost = optionalPost.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Board reply = Board.builder()
                .memberId(memberId)
                .rootId(dto.getRootId())
                .parentId(dto.getParentId())
                .nickname(dto.getNickname())
                .content(dto.getContent())
                .likeCount(0)
                .build();

        boardRepository.save(reply);

        existingPost.setReplyCount(existingPost.getReplyCount() + 1);
        boardRepository.save(existingPost);
    }
}
