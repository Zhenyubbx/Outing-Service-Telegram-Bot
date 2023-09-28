package com.skipper.entity;

import com.skipper.entity.utils.Address;
import com.skipper.entity.utils.BusinessHour;
import com.skipper.entity.utils.Review;
import com.skipper.utils.StringUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class STBData {
    private String uuid;
    private String name;
    private String description;
    private String body;
    private Double rating;
    private Address address;
    private List<BusinessHour> businessHour;
    private List<Review> reviews;
}