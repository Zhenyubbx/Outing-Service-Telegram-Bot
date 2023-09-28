package com.skipper.feign;

import com.skipper.dto.apidto.STBDataResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="stb", url="https://api.stb.gov.sg/content")
public interface STBFeignClient {

    @GetMapping(value = "/common/v2/search")
    STBDataResponseDTO getDataset(@RequestHeader("X-API-Key") String apiKey,
                                  @RequestParam(value = "dataset") String dataset,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", required = false) String sortBy,
                                  @RequestParam(value = "offset", required = false) String offset,
                                  @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping(value="/attractions/v2/search")
    STBDataResponseDTO getAttractionDetails(@RequestHeader("X-API-Key") String apiKey,
                                            @RequestParam(value="searchType") String searchType,
                                            @RequestParam(value = "searchValues") String searchValues,
                                            @RequestParam(value="sort", required = false) String sortBy,
                                            @RequestParam(value="offset", required = false) String offset,
                                            @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping(value="/food-beverages/v2/search")
    STBDataResponseDTO getFNBDetails(@RequestHeader("X-API-Key") String apiKey,
                                     @RequestParam(value="searchType") String searchType,
                                     @RequestParam(value = "searchValues") String searchValues,
                                     @RequestParam(value="sort", required = false) String sortBy,
                                     @RequestParam(value="offset", required = false) String offset,
                                     @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping(value="/bars-clubs/v2/search")
    STBDataResponseDTO getBarsAndClubsDetails(@RequestHeader("X-API-Key") String apiKey,
                                              @RequestParam(value="searchType") String searchType,
                                              @RequestParam(value = "searchValues") String searchValues,
                                              @RequestParam(value="sort", required = false) String sortBy,
                                              @RequestParam(value="offset", required = false) String offset,
                                              @RequestParam(value = "limit", required = false) Integer limit);

}
