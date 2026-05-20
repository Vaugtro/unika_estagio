package com.desafio.estagio.wicket.model;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.model.ClienteJuridico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@SuperBuilder
@Getter
@Setter
public class ClienteJuridicoModel extends ClienteJuridico implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteJuridicoModel() {
    }

    public ClienteJuridicoModel(ClienteJuridicoResponse cliente) {
        this.setId(cliente.id());
        this.setRazaoSocial(cliente.razaoSocial());
        this.setCnpj(cliente.cnpj());
        this.setInscricaoEstadual(cliente.inscricaoEstadual());
        this.setEmail(cliente.email());
        this.setEstaAtivo(cliente.estaAtivo());
        this.setDataCriacaoEmpresa(cliente.dataCriacaoEmpresa());
    }

    @Override
    @NotNull(message = "Razão Social é obrigatória")
    @Size(min = 3, message = "Razão Social deve ter pelo menos 3 caracteres")
    public String getRazaoSocial() {
        return super.getRazaoSocial();
    }

    @Override
    @NotNull(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    public String getEmail() {
        return super.getEmail();
    }
}
