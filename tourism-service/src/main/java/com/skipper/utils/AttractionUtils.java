package com.skipper.utils;

import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.enumeration.apis.stb.Dataset;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AttractionUtils {
    private final STBDataUtils stbDataUtils;
    private final GMapsUtils gMapsUtils;

    @Cacheable("nearestAttractions")
    public List<STBDataMapsDTO> getNearestAttractions(String currLocation) {
        List<STBDataDTO> listOfAttractions = stbDataUtils.getAllSTBDataByDataset(Dataset.Attractions);
        return gMapsUtils.sortSTBDataByLocation(currLocation, listOfAttractions);
    }
}