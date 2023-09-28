package com.skipper.service;

import com.skipper.dto.BaseResponseDTO;
import com.skipper.dto.PageResponseDTO;
import com.skipper.dto.STBDataDTO;
import com.skipper.dto.STBDataMapsDTO;

public interface BarsAndClubsService {
    BaseResponseDTO<PageResponseDTO<STBDataMapsDTO>> getNearestBarsAndClubsByPage(Integer pageSize, Integer pageNo, String currLocation);

    BaseResponseDTO<STBDataDTO> getBarsAndClubsDetailsByUUID(String uuid);

    BaseResponseDTO<PageResponseDTO<STBDataDTO>> getBarsAndClubsByKeyword(Integer pageSize, Integer pageNo, String keyword);
}
