package com.skipper.controller;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.service.BarsAndClubsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1.0/fnb-service/bars-clubs")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class BarsAndClubsController {
    private final BarsAndClubsService barsAndClubsService;

    @GetMapping(value="")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>>> getNearestBarsAndClubs(@RequestParam(required = false) Integer pageSize,
                                                                                                   @RequestParam(required = false) Integer pageNo,
                                                                                                   @RequestParam String currLocation) {
        log.info("Getting Bars and Clubs based on location : {}", currLocation);
        BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> res = barsAndClubsService.getNearestBarsAndClubsByPage(pageSize, pageNo, currLocation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/uuid/{uuid}")
    public ResponseEntity<BaseResponseDTO<STBDataDTO>> getBarsAndClubsDetailsByUUID(@PathVariable String uuid) {
        log.info("Getting bars and clubs details based on uuid : {}", uuid);
        BaseResponseDTO<STBDataDTO> res = barsAndClubsService.getBarsAndClubsDetailsByUUID(uuid);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/keyword/{keyword}")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataDTO>>> getBarsAndClubsByKeyword(@RequestParam(required = false) Integer pageSize,
                                                                                         @RequestParam(required = false) Integer pageNo,
                                                                                         @PathVariable String keyword) {
        log.info("Getting bars and clubs based on keyword : {}", keyword);
        BaseResponseDTO<PageResponseDTO<STBDataDTO>> res = barsAndClubsService.getBarsAndClubsByKeyword(pageSize, pageNo, keyword);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
