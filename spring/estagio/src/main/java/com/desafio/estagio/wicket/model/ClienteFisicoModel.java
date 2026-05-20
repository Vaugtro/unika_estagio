package com.desafio.estagio.wicket.model;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.model.ClienteFisico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ClienteFisicoModel extends ClienteFisico implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteFisicoModel(ClienteFisicoResponse cliente) {
        this.setId(cliente.id());
        this.setNome(cliente.nome());
        this.setCpf(cliente.cpf());
        this.setEmail(cliente.email());
        this.setEstaAtivo(cliente.estaAtivo());
    }
}


