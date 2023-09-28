package com.skipper.service;


import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;
import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;

public interface AttractionService {
    BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestAttractions(Integer pageSize, Integer pageNo, String currLocation);

    BaseResponseDTO<STBDataDTO> getAttractionDetailsByUUID(String uuid);

    BaseResponseDTO<PageResponseDTO<STBDataDTO>> getAttractionsByKeyword(Integer pageSize, Integer pageNo, String keyword);

}
