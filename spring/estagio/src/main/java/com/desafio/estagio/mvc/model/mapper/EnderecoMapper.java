package com.desafio.estagio.mvc.model.mapper;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.model.entity.Endereco;
import com.desafio.estagio.mvc.model.entity.EnderecoEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = EnderecoEntity.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EnderecoMapper {

    EnderecoDTO.Response toResponse(Endereco entity);

    @Mapping(target = "id", ignore = true)
    EnderecoEntity toEntity(EnderecoDTO.Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EnderecoDTO.Request request, @MappingTarget EnderecoEntity entity);
}
