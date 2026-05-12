package com.desafio.estagio.mapper;

import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.model.Endereco;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnderecoMapper {

    // ========== Response Mappings ==========

    @Mapping(target = "clienteId", source = "cliente.id")
    EnderecoDTO.Response toResponse(Endereco entity);

    @Mapping(target = "clienteId", source = "cliente.id")
    EnderecoDTO.ListResponse toListResponse(Endereco entity);

    // ========== Create Mappings ==========

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "principal", defaultValue = "false")
    Endereco toEntity(EnderecoDTO.CreateRequest request);

    // ========== Update Mappings ==========

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EnderecoDTO.UpdateRequest request, @MappingTarget Endereco entity);
}