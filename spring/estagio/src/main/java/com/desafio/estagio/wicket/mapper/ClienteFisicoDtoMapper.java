package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class ClienteFisicoDtoMapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ClienteFisicoDtoMapper() {
    }

    public static ClienteFisicoCreateRequest toCreateRequest(ClienteFisicoCreateFormModel formModel) {
        List<EnderecoWithinClienteCreateRequest> enderecosDTO = new ArrayList<>();
        for (EnderecoCreateFormModel endForm : formModel.getEnderecos()) {
            enderecosDTO.add(EnderecoDtoMapper.toWithinClienteCreateRequest(endForm));
        }

        String dataNascimentoStr = formModel.getDataNascimento();
        LocalDate dataNascimento = null;
        if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
            dataNascimento = LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        String rgClean = formModel.getRg() != null ? formModel.getRg().replaceAll("\\D", "") : null;

        return new ClienteFisicoCreateRequest(
                formModel.getCpf(),
                formModel.getNome(),
                rgClean,
                formModel.getEmail(),
                dataNascimento,
                enderecosDTO
        );
    }
}
