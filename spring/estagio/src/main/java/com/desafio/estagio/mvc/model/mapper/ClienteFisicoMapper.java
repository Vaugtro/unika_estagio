package com.desafio.estagio.mvc.model.mapper;

import com.desafio.estagio.mvc.model.dto.ClienteFisicoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = ClienteFisicoEntity.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClienteFisicoMapper {

    ClienteFisicoDTO.Response toResponse(ClienteFisico entity);

    @Mapping(target = "id", ignore = true)
    ClienteFisicoEntity toEntity(ClienteFisicoDTO.Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ClienteFisicoDTO.Request request, @MappingTarget ClienteFisicoEntity entity);
}
