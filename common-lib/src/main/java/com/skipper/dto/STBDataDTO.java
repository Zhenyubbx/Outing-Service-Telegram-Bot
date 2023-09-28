package com.skipper.dto;

import com.skipper.entity.utils.Address;
import com.skipper.entity.utils.BusinessHour;
import com.skipper.entity.utils.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class STBDataDTO {
    private String uuid;
    private String name;
    private String description;
    private String body;
    private Double rating;
    private Address address;
    private List<BusinessHour> businessHour;
    private List<Review> reviews;
}
