package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.LikeDTO;
import net.stockkid.stockkidbe.dto.PostReplyDTO;

public interface ReplyService {

    void register(PostReplyDTO dto);

    void modify(PostReplyDTO dto);

    void delete(Long replyId);

    void like(LikeDTO likeDTO);
}
