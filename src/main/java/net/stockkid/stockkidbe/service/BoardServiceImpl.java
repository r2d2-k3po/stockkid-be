package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.BoardPageDTO;
import net.stockkid.stockkidbe.dto.BoardReplyDTO;
import net.stockkid.stockkidbe.dto.LikeDTO;
import net.stockkid.stockkidbe.dto.PostBoardDTO;
import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.BoardCategory;
import net.stockkid.stockkidbe.entity.MemberInfo;
import net.stockkid.stockkidbe.entity.Reply;
import net.stockkid.stockkidbe.repository.BoardRepository;
import net.stockkid.stockkidbe.repository.MemberInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional
        ;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final MemberInfoRepository memberInfoRepository;

    // USER
    @Override
    public void register(PostBoardDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MemberInfo memberInfo = memberInfoRepository.getReferenceById(memberId);

        Board board = new Board();
        board.setBoardCategory(BoardCategory.valueOf(dto.getBoardCategory()));
        board.setNickname(dto.getNickname());
        board.setTitle(dto.getTitle());
        board.setPreview(dto.getPreview());
        board.setContent(dto.getContent());
        board.setTag1(dto.getTag1());
        board.setTag2(dto.getTag2());
        board.setTag3(dto.getTag3());
        board.setMemberInfo(memberInfo);

        memberInfo.getBoardList().add(board);

        boardRepository.save(board);
    }

    @Override
    public void modify(PostBoardDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Board> optionalBoard = boardRepository.findById(dto.getBoardId());
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        if (Objects.equals(existingBoard.getMemberInfo().getMemberId(), memberId)) {
            existingBoard.setBoardCategory(BoardCategory.valueOf(dto.getBoardCategory()));
            existingBoard.setNickname(dto.getNickname());
            existingBoard.setTitle(dto.getTitle());
            existingBoard.setPreview(dto.getPreview());
            existingBoard.setContent(dto.getContent());
            existingBoard.setTag1(dto.getTag1());
            existingBoard.setTag2(dto.getTag2());
            existingBoard.setTag3(dto.getTag3());

            boardRepository.save(existingBoard);
        } else throw new IllegalArgumentException("memberId not match");
    }

    @Override
    public void delete(Long boardId) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        if (Objects.equals(existingBoard.getMemberInfo().getMemberId(), memberId)) {
            existingBoard.setTitle("deleted");
            existingBoard.setPreview("deleted");
            existingBoard.setContent("deleted");

            boardRepository.save(existingBoard);
        } else throw new IllegalArgumentException("memberId not match");
    }

    @Override
    public BoardPageDTO readPage(int page, int size, String boardCategory, String sortBy) {

        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<Board> boardPage;
        if (Objects.equals(boardCategory, "ALL")) {
            boardPage = boardRepository.findAll(pageable);
        } else {
            boardPage = boardRepository.findByBoardCategory(BoardCategory.valueOf(boardCategory), pageable);
        }

        BoardPageDTO boardPageDTO = new BoardPageDTO();
        boardPageDTO.setTotalPages(boardPage.getTotalPages());
        boardPageDTO.setBoardDTOList(boardPage.stream().map(this::entityToPreviewDto).toList());

        return boardPageDTO;
    }

    @Override
    public BoardReplyDTO read(Long boardId) {

        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        existingBoard.addReadCount();

        BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
        boardReplyDTO.setBoardDTO(entityToDto(existingBoard));

        List<Reply> replyList = existingBoard.getReplyList();
        boardReplyDTO.setReplyDTOList(replyList.stream().map(this::replyToReplyDto).toList());

        boardRepository.save(existingBoard);

        return boardReplyDTO;
    }

    @Override
    public void like(LikeDTO likeDTO) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MemberInfo> optionalMemberInfo = memberInfoRepository.findById(memberId);
        MemberInfo existingMemberInfo = optionalMemberInfo.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        Optional<Board> optionalBoard = boardRepository.findById(likeDTO.getId());
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        Set<Board> likeBoardSet = existingMemberInfo.getLikeBoardSet();

        if (likeBoardSet.contains(existingBoard)) {
            throw new IllegalArgumentException("like already counted");
        } else {
            int number = likeDTO.getNumber();
            if (number == 1 || number == -1 ) {
                likeBoardSet.add(existingBoard);
                existingBoard.addLikeCount(number);
                existingBoard.getMemberInfo().addLikeCount(number);

                memberInfoRepository.save(existingMemberInfo);
                boardRepository.save(existingBoard);
            } else {
                throw new IllegalArgumentException("illegal like number");
            }
        }
    }
}
