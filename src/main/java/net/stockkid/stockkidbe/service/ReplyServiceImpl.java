package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.IdDTO;
import net.stockkid.stockkidbe.dto.LikeDTO;
import net.stockkid.stockkidbe.dto.SaveReplyDTO;
import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.MemberInfo;
import net.stockkid.stockkidbe.entity.Reply;
import net.stockkid.stockkidbe.repository.BoardRepository;
import net.stockkid.stockkidbe.repository.MemberInfoRepository;
import net.stockkid.stockkidbe.repository.ReplyRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberInfoRepository memberInfoRepository;

    @Override
    public IdDTO register(SaveReplyDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MemberInfo memberInfo = memberInfoRepository.getReferenceById(memberId);

        Optional<Board> optionalBoard = boardRepository.findById(dto.getBoardId());
        Board existingBoard = optionalBoard.orElseThrow(() -> new IllegalArgumentException("boardId not found"));

        Reply reply = new Reply();
        reply.setParentId(dto.getParentId());
        reply.setNickname(dto.getNickname());
        reply.setContent(dto.getContent());
        reply.setMemberInfo(memberInfo);
        reply.setBoard(existingBoard);

        memberInfo.getReplyList().add(reply);

        existingBoard.getReplyList().add(reply);
        existingBoard.addReplyCount();
        boardRepository.save(existingBoard);

        replyRepository.save(reply);
        return new IdDTO(reply.getId());
    }

    @Override
    public void modify(SaveReplyDTO dto) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Reply> optionalReply = replyRepository.findById(dto.getReplyId());
        Reply existingReply = optionalReply.orElseThrow(() -> new IllegalArgumentException("replyId not found"));

        if (Objects.equals(existingReply.getMemberInfo().getMemberId(), memberId)) {
            existingReply.setNickname(dto.getNickname());
            existingReply.setContent(dto.getContent());

            replyRepository.save(existingReply);
        } else throw new IllegalArgumentException("memberId not match");
    }

    @Override
    public void delete(Long replyId) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Reply> optionalReply = replyRepository.findById(replyId);
        Reply existingReply = optionalReply.orElseThrow(() -> new IllegalArgumentException("replyId not found"));

        if (Objects.equals(existingReply.getMemberInfo().getMemberId(), memberId)) {
            existingReply.setContent("deleted");

            replyRepository.save(existingReply);
        } else throw new IllegalArgumentException("memberId not match");
    }

    @Override
    public void like(LikeDTO likeDTO) {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MemberInfo> optionalMemberInfo = memberInfoRepository.findById(memberId);
        MemberInfo existingMemberInfo = optionalMemberInfo.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        Optional<Reply> optionalReply = replyRepository.findById(likeDTO.getId());
        Reply existingReply = optionalReply.orElseThrow(() -> new IllegalArgumentException("replyId not found"));

        Set<Reply> likeReplySet = existingMemberInfo.getLikeReplySet();

        if (likeReplySet.contains(existingReply)) {
            throw new IllegalArgumentException("like already counted");
        } else {
            int number = likeDTO.getNumber();
            if (number == 1 || number == -1 ) {
                likeReplySet.add(existingReply);
                existingReply.addLikeCount(number);
                existingReply.getMemberInfo().addLikeCount(number);

                memberInfoRepository.save(existingMemberInfo);
                replyRepository.save(existingReply);
            } else {
                throw new IllegalArgumentException("illegal like number");
            }
        }
    }
}
