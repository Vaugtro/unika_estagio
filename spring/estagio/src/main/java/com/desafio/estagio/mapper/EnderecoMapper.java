package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.model.EnderecoEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnderecoMapper {

    // Response mapping - IMPORTANT: ignore cliente to break circular reference
    @Mapping(target = "cliente", ignore = true)
    // This breaks the circular reference!
    EnderecoDTO.Response toResponse(EnderecoEntity entity);

    // List mapping
    List<EnderecoDTO.Response> toResponseList(List<EnderecoEntity> entities);

    // Request to Entity - ignore id and cliente (cliente will be set in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)  // Don't map cliente from request
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EnderecoEntity toEntity(EnderecoDTO.Request request);

    // Update mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)  // Don't update cliente reference
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EnderecoDTO.Request request, @MappingTarget EnderecoEntity entity);
}