package com.skipper.utils;

import com.skipper.config.FeignConfig;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.dto.apidto.STBDataResponseDTO;
import com.skipper.entity.STBData;
import com.skipper.enumeration.apis.stb.Dataset;
import com.skipper.feign.STBFeignClient;
import com.skipper.mappers.STBDataMapper;
import com.skipper.utils.STBDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FNBUtils {

    private final STBDataUtils stbDataUtils;
    private final GMapsUtils gMapsUtils;
    @Cacheable("nearestFNBs")
    public List<STBDataMapsDTO> getNearestFNBs(String currLocation) {
        log.info("Getting nearest FNBs...");
        List<STBDataDTO> stbDataDTOList = stbDataUtils.getAllSTBDataByDataset(Dataset.FoodBeverages);
        return gMapsUtils.sortSTBDataByLocation(currLocation, stbDataDTOList);

    }

}
