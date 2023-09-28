package com.skipper.service.impl;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.enumeration.apis.stb.Dataset;
import com.skipper.exception.ErrorCodes;
import com.skipper.exception.InternalException;
import com.skipper.service.FNBService;
import com.skipper.utils.FNBUtils;
import com.skipper.utils.GMapsUtils;
import com.skipper.utils.STBDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FNBServiceImpl implements FNBService {

    private final FNBUtils fnbUtils;

    private final GMapsUtils gMapsUtils;

    private final STBDataUtils stbDataUtils;

    private static final int STB_API_LIMIT = 50;

    public BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestFNBS(Integer pageSize, Integer pageNo, String currLocation) {
        List<STBDataMapsDTO> nearestFNBs = fnbUtils.getNearestFNBs(currLocation);
        List<STBDataMapsDTO> nearestFNBsPage = new ArrayList<>();


        if (pageNo == null) {
            pageNo = 0;
        }

        if (pageSize == null) {
            pageSize = Integer.MAX_VALUE;
        }

        for (int idx = pageNo * pageSize; idx < (pageNo + 1) * pageSize && idx < nearestFNBs.size(); idx++) {
            nearestFNBsPage.add(nearestFNBs.get(idx));
        }

        PageResponseDTO pageResponseDTO = PageResponseDTO.<STBDataMapsDTO>builder()
                .totalPage(nearestFNBs.size() / pageSize)
                .totalSize(Long.valueOf(nearestFNBs.size()))
                .pageSize(nearestFNBsPage.size())
                .data(nearestFNBsPage)
                .build();


        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), pageResponseDTO);
    }

    @Cacheable("fnbDetailsByUUID")
    public BaseResponseDTO<STBDataDTO> getFNBDetailsByUUID(String uuid) {
        STBDataDTO stbDataDTO = stbDataUtils.getSTBDataDetailsByUUID(Dataset.FoodBeverages, uuid);
        if (stbDataDTO != null) {
            return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), stbDataDTO);
        } else {
            log.error("No FNB details retrieved based on UUID {} provided", uuid);
            throw new InternalException("No FNB details retrieved based on UUID provided", ErrorCodes.COM_002);
        }

    }

    @Cacheable("fnbsByKeyword")
    public BaseResponseDTO<PageResponseDTO<STBDataDTO>> getFNBsByKeyword(Integer pageSize, Integer pageNo, String keyword) {
        List<STBDataDTO> sortedAttractionsPage = new ArrayList<>();
        List<STBDataDTO> STBDataDTOList = stbDataUtils.getSTBDatasByKeyword(Dataset.FoodBeverages, keyword);

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
                .totalPage(STBDataDTOList.size() / pageSize)
                .totalSize(Long.valueOf(STBDataDTOList.size()))
                .pageSize(sortedAttractionsPage.size())
                .data(sortedAttractionsPage)
                .build();
        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), pageResponseDTO);
    }

//    private List<STBDataMapsDTO>


}
