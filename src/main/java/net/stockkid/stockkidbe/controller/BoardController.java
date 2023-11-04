package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.service.BoardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@Log4j2
@RequestMapping("/api/access/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/registerPost")
    public ResponseEntity<ResponseDTO> registerPost(@RequestBody PostDTO postDTO) {

        log.info("--------------registerPost--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            boardService.registerPost(postDTO);
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

    @PutMapping("/modifyPost")
    public ResponseEntity<ResponseDTO> modifyPost(@RequestBody PostDTO postDTO) {

        log.info("--------------modifyPost--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            boardService.modifyPost(postDTO);
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

    @PostMapping("/registerReply")
    public ResponseEntity<ResponseDTO> registerReply(@RequestBody ReplyDTO replyDTO) {

        log.info("--------------registerReply--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            boardService.registerReply(replyDTO);
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

    @PutMapping("/modifyReply")
    public ResponseEntity<ResponseDTO> modifyReply(@RequestBody ReplyDTO replyDTO) {

        log.info("--------------modifyReply--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            boardService.modifyReply(replyDTO);
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

    @PatchMapping("/delete/{boardId}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable("boardId") Long boardId) {

        log.info("--------------delete boardId--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            boardService.delete(boardId);
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

    @GetMapping("/readPage")
    public ResponseEntity<ResponseDTO> readPage(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "boardCategory", defaultValue = "ALL") String boardCategory, @RequestParam(value = "sortBy", defaultValue = "boardId") String sortBy) {

        log.info("--------------readPage--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            BoardPageDTO boardPageDTO = boardService.readPage(page, size, boardCategory, sortBy);
            responseDTO.setApiStatus(ResponseStatus.READ_OK);
            responseDTO.setApiMsg("Read OK");
            responseDTO.setApiObj(boardPageDTO);
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Read Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.READ_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

}
