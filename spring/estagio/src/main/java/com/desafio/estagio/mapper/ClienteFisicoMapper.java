package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.clientefisico.*;
import com.desafio.estagio.model.ClienteFisico;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = EnderecoMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClienteFisicoMapper {

    // Response mappings
    @Mapping(target = "tipo", constant = "FISICA")
    ClienteFisicoResponse toResponse(ClienteFisico entity);

    ClienteFisicoListResponse toListResponse(ClienteFisico entity);

    ClienteFisicoReportResponse toReportResponse(ClienteFisico entity);

    // Create mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", constant = "FISICA")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", constant = "true")
    @Mapping(target = "enderecos", ignore = true) // Handled by EnderecoService
    ClienteFisico toEntity(ClienteFisicoCreateRequest request);

    // Update mapping (partial update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "rg", ignore = true)
    @Mapping(target = "cpf", ignore = true) // CPF should never change
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estaAtivo", ignore = true)
    @Mapping(target = "enderecos", ignore = true) // Handled by EnderecoService
    @Mapping(target = "enderecoPrincipal", ignore = true)
    void updateEntity(ClienteFisicoUpdateRequest request, @MappingTarget ClienteFisico entity);
}