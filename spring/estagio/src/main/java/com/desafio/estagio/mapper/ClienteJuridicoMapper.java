package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.ClienteJuridicoDTO;
import com.desafio.estagio.model.ClienteJuridico;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = EnderecoMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClienteJuridicoMapper {

    // Response mappings
    @Mapping(target = "tipo", constant = "JURIDICA")
    ClienteJuridicoDTO.Response toResponse(ClienteJuridico entity);

    ClienteJuridicoDTO.ListResponse toListResponse(ClienteJuridico entity);

    @Mapping(target = "tipo", constant = "JURIDICA")
    ClienteJuridicoDTO.ReportResponse toReportResponse(ClienteJuridico entity);

    // Create mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", constant = "JURIDICA")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", constant = "true")
    @Mapping(target = "enderecos", ignore = true) // Handled by EnderecoService
    ClienteJuridico toEntity(ClienteJuridicoDTO.CreateRequest request);

    // Update mapping (partial update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "cnpj", ignore = true) // CPF should never change
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", ignore = true)
    @Mapping(target = "enderecos", ignore = true) // Handled by EnderecoService
    @Mapping(target = "enderecoPrincipal", ignore = true)
    void updateEntity(ClienteJuridicoDTO.UpdateRequest request, @MappingTarget ClienteJuridico entity);
}