package com.skipper.service;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;

public interface FNBService {
    BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestFNBS(Integer pageSize, Integer pageNo, String currLocation);

    BaseResponseDTO<STBDataDTO> getFNBDetailsByUUID(String uuid);

    BaseResponseDTO<PageResponseDTO<STBDataDTO>> getFNBsByKeyword(Integer pageSize, Integer pageNo, String keyword);

}
