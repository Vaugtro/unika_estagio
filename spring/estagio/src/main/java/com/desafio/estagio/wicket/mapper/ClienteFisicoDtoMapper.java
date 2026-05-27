package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import com.desafio.estagio.wicket.model.ClienteFisicoUpdateFormModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless utility mapper for converting {@link ClienteFisicoCreateFormModel} to ClienteFisico DTOs.
 */
public final class ClienteFisicoDtoMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ClienteFisicoDtoMapper() {
    }

    /**
     * Maps a form model to a {@link ClienteFisicoCreateRequest}.
     * Cleans CPF and RG, parses dataNascimento from dd/MM/yyyy to {@link LocalDate}.
     *
     * @param form the form model with cliente fisico data
     * @return the create request DTO
     * @throws IllegalArgumentException if dataNascimento cannot be parsed
     */
    public static ClienteFisicoCreateRequest toCreateRequest(ClienteFisicoCreateFormModel form) {
        return ClienteFisicoCreateRequest.builder()
                .cpf(EnderecoDtoMapper.cleanDigits(form.getCpf()))
                .nome(form.getNome())
                .rg(EnderecoDtoMapper.cleanDigits(form.getRg()))
                .email(form.getEmail())
                .dataNascimento(parseDate(form.getDataNascimento()))
                .enderecos(mapEnderecos(form))
                .build();
    }

    /**
     * Parses a date string in dd/MM/yyyy format to {@link LocalDate}.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + dateStr + " (formato esperado: dd/MM/yyyy)", e);
        }
    }

    /**
     * Maps a form model to a {@link ClienteFisicoUpdateRequest}.
     *
     * @param form the form model with cliente fisico update data
     * @return the update request DTO
     */
    public static ClienteFisicoUpdateRequest toUpdateRequest(ClienteFisicoUpdateFormModel form) {
        return ClienteFisicoUpdateRequest.builder()
                .nome(form.getNome())
                .email(form.getEmail())
                .estaAtivo(form.getEstaAtivo())
                .build();
    }

    private static List<com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest> mapEnderecos(
            ClienteFisicoCreateFormModel form) {
        if (form.getEnderecos() == null) {
            return List.of();
        }
        return form.getEnderecos().stream()
                .map(EnderecoDtoMapper::toWithinClienteCreateRequest)
                .collect(Collectors.toList());
    }
}
