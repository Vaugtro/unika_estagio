package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.endereco.*;
import com.desafio.estagio.model.Endereco;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnderecoMapper {

    // ========== Response Mappings ==========

    @Mapping(target = "clienteId", source = "cliente.id")
    EnderecoResponse toResponse(Endereco entity);

    @Mapping(target = "clienteId", source = "cliente.id")
    EnderecoListResponse toListResponse(Endereco entity);

    // ========== Create Mappings ==========

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "principal", defaultValue = "false")
    Endereco toEntity(EnderecoCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "principal", defaultValue = "false")
    Endereco toEntity(EnderecoWithinClienteCreateRequest request);

    // ========== Update Mappings ==========

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EnderecoUpdateRequest request, @MappingTarget Endereco entity);
}