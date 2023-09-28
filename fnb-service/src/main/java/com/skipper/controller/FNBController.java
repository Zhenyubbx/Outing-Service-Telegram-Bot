package com.skipper.controller;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.service.FNBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1.0/fnb-service/food-beverage")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class FNBController {
    private final FNBService fnbService;

    @GetMapping(value="")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>>> getNearestFNB(@RequestParam(required = false) Integer pageSize,
                                                                                      @RequestParam(required = false) Integer pageNo,
                                                                                      @RequestParam String currLocation) {
        log.info("Getting FNBs based on location : {}", currLocation);
        BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> res = fnbService.getNearestFNBS(pageSize, pageNo, currLocation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/uuid/{uuid}")
    public ResponseEntity<BaseResponseDTO<STBDataDTO>> getFNBDetailsByUUID(@PathVariable String uuid) {
        log.info("Getting attraction details based on uuid : {}", uuid);
        BaseResponseDTO<STBDataDTO> res = fnbService.getFNBDetailsByUUID(uuid);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value="/keyword/{keyword}")
    public ResponseEntity<BaseResponseDTO<PageResponseDTO<STBDataDTO>>> getFNBsByKeyword(@RequestParam(required = false) Integer pageSize,
                                                                                                      @RequestParam(required = false) Integer pageNo,
                                                                                                      @PathVariable String keyword) {
        log.info("Getting attraction details based on keyword : {}", keyword);
        BaseResponseDTO<PageResponseDTO<STBDataDTO>> res = fnbService.getFNBsByKeyword(pageSize, pageNo, keyword);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
