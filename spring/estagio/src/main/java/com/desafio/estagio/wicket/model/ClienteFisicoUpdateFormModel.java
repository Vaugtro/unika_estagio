package com.desafio.estagio.wicket.model;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class ClienteFisicoUpdateFormModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private Boolean estaAtivo;

    public ClienteFisicoUpdateFormModel() {
    }

    public ClienteFisicoUpdateFormModel(ClienteFisicoResponse response) {
        this.id = response.id();
        this.nome = response.nome();
        this.cpf = response.cpf();
        this.email = response.email();
        this.estaAtivo = response.estaAtivo();
    }
}
