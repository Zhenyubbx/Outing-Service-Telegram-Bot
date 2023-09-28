package com.skipper.service.impl;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.enumeration.apis.stb.Dataset;
import com.skipper.exception.ErrorCodes;
import com.skipper.exception.InternalException;
import com.skipper.service.BarsAndClubsService;
import com.skipper.utils.BarsAndClubsUtils;
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
public class BarsAndClubsServiceImpl implements BarsAndClubsService {

    private final BarsAndClubsUtils barsAndClubsUtils;

    private final STBDataUtils stbDataUtils;

    public BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestBarsAndClubsByPage(Integer pageSize, Integer pageNo, String currLocation) {
        List<STBDataMapsDTO> sortedAttractionsPage = new ArrayList<>();
        List<STBDataMapsDTO> stbDataMapsDTOList = barsAndClubsUtils.getNearestBarsAndClubs(currLocation);
        if (pageNo == null) {
            pageNo = 0;
        }

        if (pageSize == null) {
            pageSize = Integer.MAX_VALUE;
        }

        for (int idx = pageNo * pageSize; idx < (pageNo + 1) * pageSize && idx < stbDataMapsDTOList.size(); idx++) {
            sortedAttractionsPage.add(stbDataMapsDTOList.get(idx));
        }

        PageResponseDTO pageResponseDTO = PageResponseDTO.<STBDataMapsDTO>builder()
                .totalPage(stbDataMapsDTOList.size() / pageSize)
                .totalSize(Long.valueOf(stbDataMapsDTOList.size()))
                .pageSize(sortedAttractionsPage.size())
                .data(sortedAttractionsPage)
                .build();


        return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), pageResponseDTO);
    }

    @Cacheable("barClubDetailsByUUID")
    public BaseResponseDTO<STBDataDTO> getBarsAndClubsDetailsByUUID(String uuid) {
        STBDataDTO stbDataDTO = stbDataUtils.getSTBDataDetailsByUUID(Dataset.BarsClubs, uuid);
        if (stbDataDTO != null) {
            return new BaseResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK.name(), stbDataDTO);
        } else {
            log.error("No Bar and Club details retrieved based on UUID {} provided", uuid);
            throw new InternalException("No Bar and Club details retrieved based on UUID provided", ErrorCodes.COM_002);
        }

    }

    @Cacheable("barClubsByKeyword")
    public BaseResponseDTO<PageResponseDTO<STBDataDTO>> getBarsAndClubsByKeyword(Integer pageSize, Integer pageNo, String keyword) {
        List<STBDataDTO> sortedAttractionsPage = new ArrayList<>();
        List<STBDataDTO> STBDataDTOList = stbDataUtils.getSTBDatasByKeyword(Dataset.BarsClubs, keyword);

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
}

