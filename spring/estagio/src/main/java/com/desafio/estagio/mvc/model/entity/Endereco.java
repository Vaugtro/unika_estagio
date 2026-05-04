package com.desafio.estagio.mvc.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Endereco {

    Long getId();

    void setLogradouro(String logradouro);
    String getLogradouro();

    void setNumero(Long numero);
    Long getNumero();

    void setCep(String cep);
    String getCep();

    void setBairro(String bairro);
    String getBairro();

    void setTelefone(String telefone);
    String getTelefone();

    void setCidade(String cidade);
    String getCidade();

    void setEstado(String estado);
    String getEstado();

    Boolean eEnderecoPrincipal();
    void eEnderecoPrincipalActivate();
    void eEnderecoPrincipalDeactivate();

    void setComplemento(String complemento);
    String getComplemento();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();



}
