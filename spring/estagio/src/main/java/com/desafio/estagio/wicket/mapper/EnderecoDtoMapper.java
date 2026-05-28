package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoUpdateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;

import java.io.Serial;
import java.io.Serializable;

public final class EnderecoDtoMapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private EnderecoDtoMapper() {
    }

    public static EnderecoCreateRequest toCreateRequest(EnderecoCreateFormModel formModel) {
        String cepClean = formModel.getCep() != null ? formModel.getCep().replaceAll("\\D", "") : null;
        String telefoneClean = formModel.getTelefone() != null ? formModel.getTelefone().replaceAll("\\D", "") : null;
        return new EnderecoCreateRequest(
                formModel.getLogradouro(),
                formModel.getNumero(),
                cepClean,
                formModel.getBairro(),
                telefoneClean,
                formModel.getMunicipioId(),
                formModel.getPrincipal() != null && formModel.getPrincipal(),
                formModel.getComplemento(),
                null
        );
    }

    public static EnderecoUpdateRequest toUpdateRequest(EnderecoCreateFormModel formModel) {
        String cepClean = formModel.getCep() != null ? formModel.getCep().replaceAll("\\D", "") : null;
        String telefoneClean = formModel.getTelefone() != null ? formModel.getTelefone().replaceAll("\\D", "") : null;
        return new EnderecoUpdateRequest(
                formModel.getLogradouro(),
                formModel.getNumero(),
                cepClean,
                formModel.getBairro(),
                telefoneClean,
                formModel.getMunicipioId(),
                formModel.getPrincipal() != null && formModel.getPrincipal(),
                formModel.getComplemento()
        );
    }

    public static EnderecoWithinClienteCreateRequest toWithinClienteCreateRequest(EnderecoCreateFormModel formModel) {
        String cepClean = formModel.getCep() != null ? formModel.getCep().replaceAll("\\D", "") : null;
        String telefoneClean = formModel.getTelefone() != null ? formModel.getTelefone().replaceAll("\\D", "") : null;
        return new EnderecoWithinClienteCreateRequest(
                formModel.getLogradouro(),
                formModel.getNumero(),
                cepClean,
                formModel.getBairro(),
                telefoneClean,
                formModel.getMunicipioId(),
                formModel.getPrincipal() != null && formModel.getPrincipal(),
                formModel.getComplemento()
        );
    }
}
