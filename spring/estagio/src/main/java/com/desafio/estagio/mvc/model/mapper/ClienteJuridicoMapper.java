package com.desafio.estagio.mvc.model.mapper;

import com.desafio.estagio.mvc.model.dto.ClienteJuridicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteJuridico;
import com.desafio.estagio.mvc.model.entity.ClienteJuridicoEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = ClienteJuridicoEntity.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClienteJuridicoMapper {

    ClienteJuridicoDTO.Response toResponse(ClienteJuridico entity);

    @Mapping(target = "id", ignore = true)
    ClienteJuridicoEntity toEntity(ClienteJuridicoDTO.Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ClienteJuridicoDTO.Request request, @MappingTarget ClienteJuridicoEntity entity);
}
