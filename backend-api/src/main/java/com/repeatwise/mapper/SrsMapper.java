package com.repeatwise.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.repeatwise.dto.request.srs.UpdateSrsSettingsRequest;
import com.repeatwise.dto.response.srs.SrsSettingsResponse;
import com.repeatwise.entity.SrsSettings;

/**
 * MapStruct mapper for SRS-related entities
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SrsMapper {

    /**
     * Convert SrsSettings entity to SrsSettingsResponse DTO
     */
    SrsSettingsResponse toResponse(SrsSettings srsSettings);

    /**
     * Update SrsSettings entity from UpdateSrsSettingsRequest
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalBoxes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateSrsSettingsRequest request, @MappingTarget SrsSettings srsSettings);
}
