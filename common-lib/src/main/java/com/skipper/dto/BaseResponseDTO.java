package com.skipper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDTO<T> {

    private Integer code;

    private String message;

    private T data;

    public BaseResponseDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}