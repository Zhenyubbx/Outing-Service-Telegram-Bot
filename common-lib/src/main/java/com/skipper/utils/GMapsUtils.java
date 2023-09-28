package com.skipper.utils;

import com.skipper.config.FeignConfig;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.dto.apidto.GMapsDistanceMatrixDTO;
import com.skipper.enumeration.apis.gmaps.Mode;
import com.skipper.feign.GMapsFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GMapsUtils {

    private final GMapsFeignClient googleMapsFeignClient;
    private final FeignConfig feignConfig;

    public List<STBDataMapsDTO> sortSTBDataByLocation(String currLocation, List<STBDataDTO> stbDataDTOS) {
        if (stbDataDTOS.size() > 0) {
            int batchSize = 25;
            int initialNumberOfSTBData = stbDataDTOS.size();
            List<STBDataMapsDTO> unsortedSTBDataMapsDTO = new ArrayList<>();
            for (STBDataDTO STBDataDTO : stbDataDTOS) {
                //"Singapore " + //Removed "Singapore" as 'sg' region is specified when calling GMaps API
                STBDataDTO.getAddress().setPostalCode(STBDataDTO.getAddress().getPostalCode());
            }
            for (int i = 0; i < stbDataDTOS.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, stbDataDTOS.size());
                List<STBDataDTO> currentBatch = stbDataDTOS.subList(i, endIndex);

                Pair<String, List<STBDataDTO>> pair = getDestinationString(currentBatch);

                GMapsDistanceMatrixDTO distanceMtx = googleMapsFeignClient.getDistanceMatrix(feignConfig.getGooglemapsapikey(), "Singapore " + currLocation, pair.getLeft(), Mode.Transit.getValue(), "sg");

                int noOfAttractions = distanceMtx.getRows().get(0).getElements().size();

                for (int idx=0; idx < noOfAttractions ; idx++) {
                    GMapsDistanceMatrixDTO.Element element = distanceMtx.getRows().get(0).getElements().get(idx);
                    if (element.getStatus().equals("OK")) {
                        STBDataMapsDTO STBDataMapsDTO = new STBDataMapsDTO(pair.getRight().get(idx));
                        STBDataMapsDTO.setDistance(element.getDistance());
                        STBDataMapsDTO.setDuration(element.getDuration());
                        unsortedSTBDataMapsDTO.add(STBDataMapsDTO);
                    }
                }
            }

            Collections.sort(unsortedSTBDataMapsDTO, new STBDataMapsComparator());
            log.info("{} STB Data removed due to invalid data, {} STB Data remaining", initialNumberOfSTBData - unsortedSTBDataMapsDTO.size(), unsortedSTBDataMapsDTO.size());
            return unsortedSTBDataMapsDTO;
        } else {
            return new ArrayList<>();
        }
    }

    private Pair<String, List<STBDataDTO>> getDestinationString(List<STBDataDTO> stbDataDTOList) {
        String destinations = "";
        int sizeOfAttractions = stbDataDTOList.size();
        for (int idx = sizeOfAttractions - 1; idx>=0; idx--) {
            if (stbDataDTOList.get(idx).getAddress().getPostalCode()!=null && !stbDataDTOList.get(idx).getAddress().getPostalCode().trim().equals("")) {
                destinations = stbDataDTOList.get(idx).getAddress().getPostalCode() + "|" + destinations;
            } else {
                stbDataDTOList.remove(idx);
            }
        }
        return Pair.of(destinations, stbDataDTOList);
    }

    private static class STBDataMapsComparator implements Comparator<STBDataMapsDTO> {
        // Returns STBData with shorter distance at front of list
        @Override
        public int compare(STBDataMapsDTO STBDataMapsDTO1, STBDataMapsDTO STBDataMapsDTO2) {
            int durationToTravel = STBDataMapsDTO1.getDuration().getValue() - STBDataMapsDTO2.getDuration().getValue();
            if (durationToTravel != 0) {
                return durationToTravel;
            } else {
                return STBDataMapsDTO1.getDistance().getValue() - STBDataMapsDTO2.getDistance().getValue();
            }
        }
    }




}
