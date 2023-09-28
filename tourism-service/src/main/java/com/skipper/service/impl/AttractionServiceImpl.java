package com.skipper.service.impl;

import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.enumeration.apis.stb.Dataset;
import com.skipper.utils.AttractionUtils;
import com.skipper.utils.GMapsUtils;
import com.skipper.utils.STBDataUtils;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.skipper.service.AttractionService;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionUtils attractionUtils;

    private final GMapsUtils gMapsUtils;

    private final STBDataUtils stbDataUtils;

    public BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestAttractions(Integer pageSize, Integer pageNo, String currLocation) {
        List<STBDataMapsDTO> sortedAttractionsPage = new ArrayList<>();
        List<STBDataMapsDTO> sortedAttractions = attractionUtils.getNearestAttractions(currLocation);

        if (pageNo == null) {
            pageNo = 0;
        }

        if (pageSize == null) {
            pageSize = Integer.MAX_VALUE;
        }

        for (int idx = pageNo * pageSize; idx < (pageNo + 1) * pageSize && idx < sortedAttractions.size(); idx++) {
            sortedAttractionsPage.add(sortedAttractions.get(idx));
        }

        PageResponseDTO pageResponseDTO = PageResponseDTO.<STBDataMapsDTO>builder()
                .totalPage(sortedAttractions.size()/pageSize)
                .totalSize(Long.valueOf(sortedAttractions.size()))
                .pageSize(sortedAttractionsPage.size())
                .data(sortedAttractionsPage)
                .build();


        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), pageResponseDTO);
    }

    @Cacheable("attractionDetailsByUUID")
    public BaseResponseDTO<STBDataDTO> getAttractionDetailsByUUID(String uuid) {
        STBDataDTO STBDataDTO = stbDataUtils.getSTBDataDetailsByUUID(Dataset.Attractions, uuid);
        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), STBDataDTO);
    }

    @Cacheable("attractionsByKeyword")
    public BaseResponseDTO<PageResponseDTO<STBDataDTO>> getAttractionsByKeyword(Integer pageSize, Integer pageNo, String keyword) {
        List<STBDataDTO> sortedAttractionsPage = new ArrayList<>();
        List<STBDataDTO> STBDataDTOList = stbDataUtils.getSTBDatasByKeyword(Dataset.Attractions, keyword);

        if (pageNo == null) {
            pageNo = 0;
        }

        if (pageSize == null) {
            pageSize = 50;
        }

        for (int idx = pageNo * pageSize; idx < (pageNo + 1) * pageSize && idx < STBDataDTOList.size(); idx++) {
            sortedAttractionsPage.add(STBDataDTOList.get(idx));
        }

        PageResponseDTO pageResponseDTO = PageResponseDTO.<STBDataDTO>builder()
                .totalPage(STBDataDTOList.size()/pageSize)
                .totalSize(Long.valueOf(STBDataDTOList.size()))
                .pageSize(sortedAttractionsPage.size())
                .data(sortedAttractionsPage)
                .build();
        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), pageResponseDTO);
    }
}

