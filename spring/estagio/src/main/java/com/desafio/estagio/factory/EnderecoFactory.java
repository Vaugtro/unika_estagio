package com.desafio.estagio.factory;

import com.desafio.estagio.model.Endereco;

public interface EnderecoFactory {
    Endereco createEndereco();

    Endereco cloneEndereco(Endereco source);
}