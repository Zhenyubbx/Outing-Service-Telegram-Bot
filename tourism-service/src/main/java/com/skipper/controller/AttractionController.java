package com.skipper.controller;

import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.skipper.service.AttractionService;

@RequestMapping("/v1.0/tourism-service/attractions")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class AttractionController {
    private final AttractionService attractionService;

    @GetMapping(value="")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>>> getNearestAttractions(@RequestParam(required = false) Integer pageSize,
                                                                                                  @RequestParam(required = false) Integer pageNo,
                                                                                                  @RequestParam String currLocation) {
        log.info("Getting attractions based on location : {}", currLocation);
        BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> res = attractionService.getNearestAttractions(pageSize, pageNo, currLocation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/uuid/{uuid}")
    public ResponseEntity<BaseResponseDTO<STBDataDTO>> getAttractionDetailsByUUID(@PathVariable String uuid) {
        log.info("Getting attraction details based on uuid : {}", uuid);
        BaseResponseDTO<STBDataDTO> res = attractionService.getAttractionDetailsByUUID(uuid);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/keyword/{keyword}")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataDTO>>> getAttractionDetailsByKeyword(@RequestParam(required = false) Integer pageSize,
                                                                                                      @RequestParam(required = false) Integer pageNo,
                                                                                                      @PathVariable String keyword) {
        log.info("Getting attraction details based on keyword : {}", keyword);
        BaseResponseDTO<PageResponseDTO<STBDataDTO>> res = attractionService.getAttractionsByKeyword(pageSize, pageNo, keyword);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
