package com.skipper.dto;

import com.skipper.dto.apidto.GMapsDistanceMatrixDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MapsDTO {
    //in metres
    private GMapsDistanceMatrixDTO.Distance distance;
    private GMapsDistanceMatrixDTO.Duration duration;
}
