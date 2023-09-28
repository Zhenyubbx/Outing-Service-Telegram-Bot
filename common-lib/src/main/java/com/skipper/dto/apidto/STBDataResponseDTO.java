package com.skipper.dto.apidto;

import com.skipper.entity.STBData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;


@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class STBDataResponseDTO {
    private Status status;
    private List<STBData> data;
    private Integer totalRecords;
    private Integer retrievedRecords;

    @Data
    public static class Status {
        private String code;
        private String name;
        private String message;
    }

}

