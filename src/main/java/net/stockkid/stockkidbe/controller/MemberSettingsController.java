package net.stockkid.stockkidbe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.service.MemberSettingsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@Log4j2
@RequestMapping("/api/access/memberSettings")
@RequiredArgsConstructor
public class MemberSettingsController {

    private final MemberSettingsService memberSettingsService;

    @PostMapping("/saveScreenComposition")
    public ResponseEntity<ResponseDTO> saveScreenComposition(@RequestBody ScreenCompositionDTO screenCompositionDTO) {

        log.info("--------------saveScreenComposition--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            memberSettingsService.saveScreenComposition(screenCompositionDTO);
            responseDTO.setApiStatus(ResponseStatus.SAVE_OK);
            responseDTO.setApiMsg("Save OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Save Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.SAVE_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/loadScreenSetting/{number}")
    public ResponseEntity<ResponseDTO> loadScreenSetting(@PathVariable("number") String number) {

        log.info("--------------loadScreenSetting--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ScreenSettingDTO screenSettingDTO = memberSettingsService.loadScreenSetting(number);
            responseDTO.setApiObj(screenSettingDTO);
            responseDTO.setApiStatus(ResponseStatus.LOAD_OK);
            responseDTO.setApiMsg("Load OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Load Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.LOAD_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/loadScreenSettingDefault/{number}")
    public ResponseEntity<ResponseDTO> loadScreenSettingDefault(@PathVariable("number") String number) {

        log.info("--------------loadScreenSettingDefault--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ScreenSettingDTO screenSettingDTO = memberSettingsService.loadScreenSettingDefault(number);
            responseDTO.setApiObj(screenSettingDTO);
            responseDTO.setApiStatus(ResponseStatus.LOAD_OK);
            responseDTO.setApiMsg("Load OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Load Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.LOAD_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/loadScreenTitles")
    public ResponseEntity<ResponseDTO> loadScreenTitles() {

        log.info("--------------loadScreenTitles--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ScreenTitlesDTO screenTitlesDTO = memberSettingsService.loadScreenTitles();
            responseDTO.setApiObj(screenTitlesDTO);
            responseDTO.setApiStatus(ResponseStatus.LOAD_OK);
            responseDTO.setApiMsg("Load OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Load Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.LOAD_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/loadScreenTitlesDefault")
    public ResponseEntity<ResponseDTO> loadScreenTitlesDefault() {

        log.info("--------------loadScreenTitlesDefault--------------");

        ResponseDTO responseDTO = new ResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ScreenTitlesDTO screenTitlesDTO = memberSettingsService.loadScreenTitlesDefault();
            responseDTO.setApiObj(screenTitlesDTO);
            responseDTO.setApiStatus(ResponseStatus.LOAD_OK);
            responseDTO.setApiMsg("Load OK");
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Load Error : " + e.getMessage());
            responseDTO.setApiStatus(ResponseStatus.LOAD_FAIL);
            responseDTO.setApiMsg(e.getMessage());
            return new ResponseEntity<>(responseDTO, httpHeaders, HttpStatus.CONFLICT);
        }
    }
}
