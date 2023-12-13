package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.service.ReplyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@Log4j2
@RequestMapping("/api/access/reply")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody SaveReplyDTO saveReplyDTO) {

        log.info("--------------reply register--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            IdDTO idDTO = replyService.register(saveReplyDTO);
            responseDTO.setApiStatus(ResponseStatus.WRITE_OK);
            responseDTO.setApiMsg("Write OK");
            responseDTO.setApiObj(idDTO);
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Write Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.WRITE_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<ResponseDTO> modify(@RequestBody SaveReplyDTO saveReplyDTO) {

        log.info("--------------reply modify--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            replyService.modify(saveReplyDTO);
            responseDTO.setApiStatus(ResponseStatus.WRITE_OK);
            responseDTO.setApiMsg("Write OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Write Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.WRITE_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/delete/{replyId}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable("replyId") Long replyId) {

        log.info("--------------delete replyId--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            replyService.delete(replyId);
            responseDTO.setApiStatus(ResponseStatus.WRITE_OK);
            responseDTO.setApiMsg("Write OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Write Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.WRITE_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/like")
    public ResponseEntity<ResponseDTO> like(@RequestBody LikeDTO likeDTO) {

        log.info("--------------like replyId--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            replyService.like(likeDTO);
            responseDTO.setApiStatus(ResponseStatus.WRITE_OK);
            responseDTO.setApiMsg("Write OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Write Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.WRITE_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }
}
