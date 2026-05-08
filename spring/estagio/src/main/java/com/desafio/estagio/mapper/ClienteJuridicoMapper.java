package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.model.ClienteJuridicoEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = EnderecoMapper.class,  // Use EnderecoMapper
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClienteJuridicoMapper {

    // Response mapping
    @Mapping(target = "enderecos", source = "enderecos")
    @Mapping(target = "tipo", constant = "JURIDICA")
    ClienteJuridicoDTO.Response toResponse(ClienteJuridicoEntity entity);

    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enderecos", source = "enderecos")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", constant = "true")
    ClienteJuridicoEntity toEntity(ClienteJuridicoDTO.Request request);

    // Update mapping
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ClienteJuridicoDTO.Request request, @MappingTarget ClienteJuridicoEntity entity);
}