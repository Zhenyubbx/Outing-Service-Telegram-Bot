package com.skipper.feign;

import com.skipper.dto.apidto.GMapsDistanceMatrixDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="maps", url="https://maps.googleapis.com/maps/api")
public interface GMapsFeignClient {

    @GetMapping(value = "/distancematrix/json")
    GMapsDistanceMatrixDTO getDistanceMatrix(@RequestParam("key") String key,
                                             @RequestParam("origins") String origins,
                                             @RequestParam("destinations") String destinations,
                                             @RequestParam(value = "mode", required = false) String mode,
                                             @RequestParam(value = "region", required = false) String region);

}
