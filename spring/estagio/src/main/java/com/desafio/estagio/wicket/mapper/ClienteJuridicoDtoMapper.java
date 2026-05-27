package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoUpdateRequest;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
import com.desafio.estagio.wicket.model.ClienteJuridicoUpdateFormModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless utility mapper for converting {@link ClienteJuridicoCreateFormModel} to ClienteJuridico DTOs.
 */
public final class ClienteJuridicoDtoMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ClienteJuridicoDtoMapper() {
    }

    /**
     * Maps a form model to a {@link ClienteJuridicoCreateRequest}.
     * Cleans CNPJ, parses dataCriacaoEmpresa from dd/MM/yyyy to {@link LocalDate}.
     *
     * @param form the form model with cliente juridico data
     * @return the create request DTO
     * @throws IllegalArgumentException if dataCriacaoEmpresa cannot be parsed
     */
    public static ClienteJuridicoCreateRequest toCreateRequest(ClienteJuridicoCreateFormModel form) {
        return ClienteJuridicoCreateRequest.builder()
                .cnpj(EnderecoDtoMapper.cleanDigits(form.getCnpj()))
                .razaoSocial(form.getRazaoSocial())
                .inscricaoEstadual(form.getInscricaoEstadual())
                .email(form.getEmail())
                .dataCriacaoEmpresa(parseDate(form.getDataCriacaoEmpresa()))
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
     * Maps a form model to a {@link ClienteJuridicoUpdateRequest}.
     *
     * @param form the form model with cliente juridico update data
     * @return the update request DTO
     */
    public static ClienteJuridicoUpdateRequest toUpdateRequest(ClienteJuridicoUpdateFormModel form) {
        return ClienteJuridicoUpdateRequest.builder()
                .razaoSocial(form.getRazaoSocial())
                .inscricaoEstadual(form.getInscricaoEstadual())
                .email(form.getEmail())
                .dataCriacaoEmpresa(form.getDataCriacaoEmpresa())
                .estaAtivo(form.getEstaAtivo())
                .enderecos(null)
                .build();
    }

    private static List<EnderecoCreateRequest> mapEnderecos(ClienteJuridicoCreateFormModel form) {
        if (form.getEnderecos() == null) {
            return List.of();
        }
        return form.getEnderecos().stream()
                .map(ClienteJuridicoDtoMapper::toEnderecoCreateRequest)
                .collect(Collectors.toList());
    }

    /**
     * Maps an endereco form model to an {@link EnderecoCreateRequest} without a clienteId,
     * since it will be set by the service layer after the parent cliente is created.
     */
    private static EnderecoCreateRequest toEnderecoCreateRequest(
            com.desafio.estagio.wicket.model.EnderecoCreateFormModel form) {
        return EnderecoCreateRequest.builder()
                .logradouro(form.getLogradouro())
                .numero(form.getNumero())
                .cep(EnderecoDtoMapper.cleanDigits(form.getCep()))
                .bairro(form.getBairro())
                .telefone(EnderecoDtoMapper.cleanDigits(form.getTelefone()))
                .estado(form.getEstado())
                .cidade(form.getCidade())
                .principal(form.getPrincipal())
                .complemento(form.getComplemento())
                .clienteId(null)
                .build();
    }
}
