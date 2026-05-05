package com.desafio.estagio.mvc.model.entity;

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

    void setPrincipal(Boolean principal);
    Boolean getPrincipal();
    Boolean isPrincipal();

    String getComplemento();
    void setComplemento(String complemento);

    void setCliente(Cliente cliente);
    Cliente getCliente();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();


}
