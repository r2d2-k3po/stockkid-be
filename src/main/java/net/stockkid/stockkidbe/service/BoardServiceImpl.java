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

import java.util.Objects;
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
    public void modifyPost(PostDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Board> optionalPost = boardRepository.findById(dto.getBoardId());
        Board existingPost = optionalPost.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        if (Objects.equals(existingPost.getMemberId(), memberId)) {
            existingPost.setBoardCategory(BoardCategory.valueOf(dto.getBoardCategory()));
            existingPost.setNickname(dto.getNickname());
            existingPost.setTitle(dto.getTitle());
            existingPost.setContent(dto.getContent());
            existingPost.setTag1(dto.getTag1());
            existingPost.setTag2(dto.getTag2());
            existingPost.setTag3(dto.getTag3());

            boardRepository.save(existingPost);
        } else throw new IllegalArgumentException("memberId not match");
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

    @Override
    public void modifyReply(ReplyDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Board> optionalReply = boardRepository.findById(dto.getBoardId());
        Board existingReply = optionalReply.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        if (Objects.equals(existingReply.getMemberId(), memberId)) {
            existingReply.setNickname(dto.getNickname());
            existingReply.setContent(dto.getContent());

            boardRepository.save(existingReply);
        } else throw new IllegalArgumentException("memberId not match");
    }

    @Override
    public void delete(Long boardId) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        if (Objects.equals(existingBoard.getMemberId(), memberId)) {
            if (existingBoard.getRootId() == null) {
                existingBoard.setTitle("deleted");
            }
            existingBoard.setContent("deleted");

            boardRepository.save(existingBoard);
        } else throw new IllegalArgumentException("memberId not match");
    }
}
