package com.desafio.estagio.factory;


import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.enums.TipoCliente;

public interface ClienteFactory {
    Cliente createCliente(TipoCliente tipo);
    Cliente createClienteJuridico();
    Cliente createClienteFisico();
    Cliente cloneCliente(Cliente source);
}