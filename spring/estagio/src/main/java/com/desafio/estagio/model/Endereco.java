package com.desafio.estagio.model;

import java.time.LocalDateTime;

public interface Endereco {

    Long getId();

    String getLogradouro();

    void setLogradouro(String logradouro);

    Long getNumero();

    void setNumero(Long numero);

    String getCep();

    void setCep(String cep);

    String getBairro();

    void setBairro(String bairro);

    String getTelefone();

    void setTelefone(String telefone);

    String getCidade();

    void setCidade(String cidade);

    String getEstado();

    void setEstado(String estado);

    Boolean getPrincipal();

    void setPrincipal(Boolean principal);

    Boolean isPrincipal();

    String getComplemento();

    void setComplemento(String complemento);

    Cliente getCliente();

    void setCliente(Cliente cliente);

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();


}
