package com.desafio.estagio.wicket.mapper;

import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoUpdateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;

/**
 * Stateless utility mapper for converting {@link EnderecoCreateFormModel} to Endereco DTOs.
 */
public final class EnderecoDtoMapper {

    private EnderecoDtoMapper() {
    }

    /**
     * Maps a form model to an {@link EnderecoCreateRequest}, cleaning CEP and telefone.
     *
     * @param form      the form model with endereco data
     * @param clienteId the client ID to associate the endereco with
     * @return the create request DTO
     */
    public static EnderecoCreateRequest toCreateRequest(EnderecoCreateFormModel form, Long clienteId) {
        return EnderecoCreateRequest.builder()
                .logradouro(form.getLogradouro())
                .numero(form.getNumero())
                .cep(cleanDigits(form.getCep()))
                .bairro(form.getBairro())
                .telefone(cleanDigits(form.getTelefone()))
                .estado(form.getEstado())
                .cidade(form.getCidade())
                .principal(form.getPrincipal())
                .complemento(form.getComplemento())
                .clienteId(clienteId)
                .build();
    }

    /**
     * Maps a form model to an {@link EnderecoUpdateRequest}, cleaning CEP and telefone.
     *
     * @param form the form model with endereco data
     * @return the update request DTO
     */
    public static EnderecoUpdateRequest toUpdateRequest(EnderecoCreateFormModel form) {
        return EnderecoUpdateRequest.builder()
                .logradouro(form.getLogradouro())
                .numero(form.getNumero())
                .cep(cleanDigits(form.getCep()))
                .bairro(form.getBairro())
                .telefone(cleanDigits(form.getTelefone()))
                .estado(form.getEstado())
                .cidade(form.getCidade())
                .principal(form.getPrincipal())
                .complemento(form.getComplemento())
                .build();
    }

    /**
     * Maps a form model to an {@link EnderecoWithinClienteCreateRequest} (no clienteId),
     * cleaning CEP and telefone. Used when endereco is nested within a cliente creation request.
     *
     * @param form the form model with endereco data
     * @return the within-cliente create request DTO
     */
    public static EnderecoWithinClienteCreateRequest toWithinClienteCreateRequest(EnderecoCreateFormModel form) {
        return EnderecoWithinClienteCreateRequest.builder()
                .logradouro(form.getLogradouro())
                .numero(form.getNumero())
                .cep(cleanDigits(form.getCep()))
                .bairro(form.getBairro())
                .telefone(cleanDigits(form.getTelefone()))
                .estado(form.getEstado())
                .cidade(form.getCidade())
                .principal(form.getPrincipal())
                .complemento(form.getComplemento())
                .build();
    }

    /**
     * Removes all non-digit characters from the input string.
     *
     * @param value the input string
     * @return the cleaned string with only digits, or null if input is null
     */
    public static String cleanDigits(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }
}
