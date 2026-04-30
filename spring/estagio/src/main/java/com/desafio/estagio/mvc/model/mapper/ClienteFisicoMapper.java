package com.desafio.estagio.mvc.model.mapper;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTOS;
import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.IClienteFisico;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = IClienteFisico.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClienteFisicoMapper {


    ClienteFisicoDTOS.Response toResponse(ClienteFisico entity);

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    IClienteFisico toEntity(ClienteFisicoDTOS.Request request);
}
