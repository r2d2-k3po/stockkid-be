package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.LikeDTO;
import net.stockkid.stockkidbe.dto.SaveReplyDTO;

public interface ReplyService {

    void register(SaveReplyDTO dto);

    void modify(SaveReplyDTO dto);

    void delete(Long replyId);

    void like(LikeDTO likeDTO);
}
