package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class ClienteJuridicoDtoMapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ClienteJuridicoDtoMapper() {
    }

    public static ClienteJuridicoCreateRequest toCreateRequest(ClienteJuridicoCreateFormModel formModel) {
        List<EnderecoWithinClienteCreateRequest> enderecosDTO = new ArrayList<>();
        for (EnderecoCreateFormModel endForm : formModel.getEnderecos()) {
            enderecosDTO.add(EnderecoDtoMapper.toWithinClienteCreateRequest(endForm));
        }

        String dataCriacaoStr = formModel.getDataCriacaoEmpresa();
        LocalDate dataCriacao = null;
        if (dataCriacaoStr != null && !dataCriacaoStr.isBlank()) {
            dataCriacao = LocalDate.parse(dataCriacaoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        return new ClienteJuridicoCreateRequest(
                formModel.getCnpj(),
                formModel.getRazaoSocial(),
                formModel.getInscricaoEstadual(),
                formModel.getEmail(),
                dataCriacao,
                enderecosDTO
        );
    }
}
