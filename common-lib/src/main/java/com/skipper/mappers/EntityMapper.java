package com.skipper.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDTO(E entity);

    List<E> toEntity(List<D> dtos);

    List<D> toDTO(List<E> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(D dto, @MappingTarget E entity);

}
