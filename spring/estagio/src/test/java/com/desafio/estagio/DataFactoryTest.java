package com.desafio.estagio;

import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.model.EnderecoEntity;

public class DataFactoryTest {

    public static EnderecoDTO.Request createValidEnderecoRequest() {
        return new EnderecoDTO.Request(
                "Rua das Flores",
                123L,
                "01234567",
                "Centro",
                "11912345678",
                "São Paulo",
                "SP",
                true,
                "Apto 42",
                null
        );
    }

    public static EnderecoDTO.Request createUpdatedEnderecoRequest() {
        return new EnderecoDTO.Request(
                "Rua Nova",
                456L,
                "87654321",
                "Jardins",
                "11987654321",
                "Rio de Janeiro",
                "RJ",
                false,
                "Sala 10",
                null
        );
    }

    public static ClienteFisicoEntity createValidCliente() {
        ClienteFisicoEntity cliente = new ClienteFisicoEntity();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setCpf("12345678900");
        cliente.setEstaAtivo(true);
        return cliente;
    }

    public static EnderecoEntity createValidEnderecoEntity() {
        EnderecoEntity entity = new EnderecoEntity();
        entity.setId(1L);
        entity.setLogradouro("Rua das Flores");
        entity.setNumero(123L);
        entity.setCep("01234567");
        entity.setBairro("Centro");
        entity.setTelefone("11912345678");
        entity.setCidade("São Paulo");
        entity.setEstado("SP");
        entity.setComplemento("Apto 42");
        entity.setPrincipal(true);
        entity.setCliente(createValidCliente());
        return entity;
    }
}