package com.desafio.estagio.mvc.model.entity;

import org.springframework.stereotype.Component;

@Component
public class ClienteFactoryImpl implements ClienteFactory {

    public Cliente createCliente(String type) {
        if ("JURIDICA".equalsIgnoreCase(type)) {
            return new ClienteJuridicoEntity();
        } else if ("FISICA".equalsIgnoreCase(type)) {
            return new ClienteFisicoEntity();
        }
        throw new IllegalArgumentException("Unknown client type: " + type);
    }
}
