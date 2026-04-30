package com.desafio.estagio.mvc.model.entity;

import org.springframework.stereotype.Component;

@Component
public class IClienteFactory implements ClienteFactory {

    public Cliente createCliente(String type) {
        if ("JURIDICA".equalsIgnoreCase(type)) {
            return new IClienteJuridico();
        } else if ("FISICA".equalsIgnoreCase(type)) {
            return new IClienteFisico();
        }
        throw new IllegalArgumentException("Unknown client type: " + type);
    }
}
