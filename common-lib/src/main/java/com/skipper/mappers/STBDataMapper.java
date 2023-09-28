package com.skipper.mappers;

import com.skipper.dto.STBDataDTO;
import com.skipper.entity.STBData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface STBDataMapper extends EntityMapper<STBDataDTO, STBData> {
}
