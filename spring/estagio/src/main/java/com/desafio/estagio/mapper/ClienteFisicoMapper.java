package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.model.ClienteFisicoEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = EnderecoMapper.class,  // Use EnderecoMapper for endereco mappings
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClienteFisicoMapper {

    // Response mapping - use EnderecoMapper for enderecos
    @Mapping(target = "enderecos", source = "enderecos")
    @Mapping(target = "tipo", constant = "FISICA")
    ClienteFisicoDTO.Response toResponse(ClienteFisicoEntity entity);

    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enderecos", source = "enderecos")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", constant = "true")
    ClienteFisicoEntity toEntity(ClienteFisicoDTO.Request request);

    // Update mapping
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enderecos", ignore = true)  // Handle enderecos separately
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ClienteFisicoDTO.Request request, @MappingTarget ClienteFisicoEntity entity);
}