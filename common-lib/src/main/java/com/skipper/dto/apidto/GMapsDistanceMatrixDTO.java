package com.skipper.dto.apidto;

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
public class GMapsDistanceMatrixDTO {
    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Row> rows;

    @Data
    public static class Row {
        private List<Element> elements;
    }

    @Data
    public static class Element {
        private Distance distance;
        private Duration duration;
        private String status;
    }

    @Data
    public static class Distance {
        private String text;
        private Integer value;
    }

    @Data
    public static class Duration {
        private String text;
        private Integer value;
    }

}