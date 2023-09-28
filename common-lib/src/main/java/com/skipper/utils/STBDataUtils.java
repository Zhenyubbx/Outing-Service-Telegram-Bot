package com.skipper.utils;

import com.skipper.config.FeignConfig;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.apidto.STBDataResponseDTO;
import com.skipper.entity.STBData;
import com.skipper.enumeration.apis.stb.Dataset;
import com.skipper.feign.STBFeignClient;
import com.skipper.mappers.STBDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class STBDataUtils {

    private final STBFeignClient stbFeignClient;
    private final FeignConfig feignConfig;
    private final STBDataMapper stbDataMapper;

    private static final int STB_API_LIMIT = 50;

    public List<STBDataDTO> getAllSTBDataByDataset(Dataset dataset) {
        STBDataResponseDTO response = stbFeignClient.getDataset(feignConfig.getStbapikey(), dataset.getValue(),null, null,null,STB_API_LIMIT);
        List<STBDataDTO> stbDataDTOList = new ArrayList<>(stbDataMapper.toDTO(response.getData()));

        int totalRecords = response.getRetrievedRecords();
        while (totalRecords < response.getTotalRecords()) {
            response = stbFeignClient.getDataset(feignConfig.getStbapikey(), dataset.getValue(), null,null, String.valueOf(totalRecords), STB_API_LIMIT);
            totalRecords += response.getRetrievedRecords();
            stbDataDTOList.addAll(stbDataMapper.toDTO(response.getData()));
        }

        log.info("Number of data retrieved from STB API: {}", stbDataDTOList.size());
        List<STBDataDTO> stbDataDTOListNoDuplicates = removeDuplicatesByName(stbDataDTOList);
        stbDataDTOListNoDuplicates.forEach(stbDataDTO -> stbDataDTO.setBody(StringUtils.removeHtmlTags(stbDataDTO.getBody())));
        log.info("Number of unique data: {}", stbDataDTOListNoDuplicates.size());
        return stbDataDTOListNoDuplicates;
    }

    public STBDataDTO getSTBDataDetailsByUUID(Dataset dataset, String uuid) {
        STBDataResponseDTO stbDataResponseDTO = null;
        if (dataset.equals(Dataset.FoodBeverages)) {
            stbDataResponseDTO = stbFeignClient.getFNBDetails(feignConfig.getStbapikey(), "uuids", uuid, null, null, null);
        } else if (dataset.equals(Dataset.Attractions)) {
            stbDataResponseDTO = stbFeignClient.getAttractionDetails(feignConfig.getStbapikey(), "uuids", uuid, null, null, null);
        } else if (dataset.equals(Dataset.BarsClubs)) {
            stbDataResponseDTO = stbFeignClient.getBarsAndClubsDetails(feignConfig.getStbapikey(), "uuids", uuid, null, null, null);
        }
        if (stbDataResponseDTO != null && stbDataResponseDTO.getData().size() > 0) {
            STBData stbData = stbDataResponseDTO.getData().get(0);
            stbData.setBody(StringUtils.removeHtmlTags(stbData.getBody()));
            return stbDataMapper.toDTO(stbData);
        } else {
            return null;
        }
    }

    public List<STBDataDTO> getSTBDatasByKeyword(Dataset dataset, String keyword) {
        STBDataResponseDTO stbDataResponseDTO = null;

        if (dataset.equals(Dataset.FoodBeverages)) {
            stbDataResponseDTO = stbFeignClient.getFNBDetails(feignConfig.getStbapikey(), "keyword", keyword, null, null, STB_API_LIMIT);
        } else if (dataset.equals(Dataset.Attractions)) {
            stbDataResponseDTO = stbFeignClient.getAttractionDetails(feignConfig.getStbapikey(), "keyword", keyword, null, null, STB_API_LIMIT);
        } else if (dataset.equals(Dataset.BarsClubs)) {
            stbDataResponseDTO = stbFeignClient.getBarsAndClubsDetails(feignConfig.getStbapikey(), "keyword", keyword, null, null, STB_API_LIMIT);
        }

        if (stbDataResponseDTO != null ) {
            List<STBDataDTO> stbDataDTOList = new ArrayList<>(stbDataMapper.toDTO(stbDataResponseDTO.getData()));
            List<STBDataDTO> stbDataDTOListNoDuplicates = removeDuplicatesByName(stbDataDTOList);
            stbDataDTOListNoDuplicates.forEach(stbDataDTO -> stbDataDTO.setBody(StringUtils.removeHtmlTags(stbDataDTO.getBody())));
            log.info("Number of unique data: {}", stbDataDTOListNoDuplicates.size());
            return stbDataDTOListNoDuplicates;
        } else {
            return null;
        }
    }

    private static List<STBDataDTO> removeDuplicatesByName(List<STBDataDTO> STBDataDTOList) {
        Set<String> uniqueSTBDataNames = new HashSet<>();
        List<STBDataDTO> uniqueSTBDataList = new ArrayList<>();

        for (STBDataDTO stbDataDTO1 : STBDataDTOList) {
            String stbDataDTO1Name = StringUtils.removeSpecialCharactersAndPlural(stbDataDTO1.getName());
            if ((uniqueSTBDataNames.size() == 0 || uniqueSTBDataNames.stream().noneMatch(uniqueSTBDataName -> uniqueSTBDataName.contains(stbDataDTO1Name) || stbDataDTO1Name.contains(uniqueSTBDataName))) && uniqueSTBDataNames.add(stbDataDTO1Name) ) {
                uniqueSTBDataList.add(stbDataDTO1);
            }
        }
        return uniqueSTBDataList;
    }
}
