package com.repeatwise.mapper;

import java.util.List;

/**
 * Generic base mapper interface for common mapping operations
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface BaseMapper<E, D> {

    /**
     * Map entity to DTO
     * @param entity the entity to map
     * @return the mapped DTO
     */
    D toDto(E entity);

    /**
     * Map DTO to entity
     * @param dto the DTO to map
     * @return the mapped entity
     */
    E toEntity(D dto);

    /**
     * Map list of entities to list of DTOs
     * @param entities the list of entities to map
     * @return the mapped list of DTOs
     */
    List<D> toDtoList(List<E> entities);

    /**
     * Map list of DTOs to list of entities
     * @param dtos the list of DTOs to map
     * @return the mapped list of entities
     */
    List<E> toEntityList(List<D> dtos);

    /**
     * Update entity with DTO data
     * @param entity the entity to update
     * @param dto the DTO containing update data
     * @return the updated entity
     */
    E updateEntity(E entity, D dto);
} 
