package com.desafio.estagio.factory;

import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.ClienteFisico;
import com.desafio.estagio.model.ClienteJuridico;
import com.desafio.estagio.model.Endereco;
import com.desafio.estagio.model.enums.TipoCliente;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class ClienteFactoryImpl implements ClienteFactory {

    private final EnderecoFactory enderecoFactory;
    private final Map<TipoCliente, Supplier<Cliente>> creators;

    public ClienteFactoryImpl(EnderecoFactory enderecoFactory) {
        this.enderecoFactory = enderecoFactory;
        this.creators = new EnumMap<>(TipoCliente.class);
        this.creators.put(TipoCliente.FISICA, ClienteFisico::new);
        this.creators.put(TipoCliente.JURIDICA, ClienteJuridico::new);
    }

    @Override
    public Cliente createCliente(TipoCliente tipo) {
        Supplier<Cliente> creator = creators.get(tipo);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported client type: " + tipo);
        }

        Cliente cliente = creator.get();
        cliente.setTipo(tipo);

        // Cria com um endereço inicial
        Endereco enderecoInicial = enderecoFactory.createEndereco();
        cliente.addEndereco(enderecoInicial);

        return cliente;
    }

    @Override
    public Cliente createClienteFisico() {
        return createCliente(TipoCliente.FISICA);
    }

    @Override
    public Cliente createClienteJuridico() {
        return createCliente(TipoCliente.JURIDICA);
    }

    @Override
    public Cliente cloneCliente(Cliente source) {
        if (source == null) return null;

        Cliente clone = createCliente(source.getTipo());

        // Atributos comuns
        clone.setEmail(source.getEmail());
        clone.setEstaAtivo(source.getEstaAtivo());

        // Atributos específicos (polymorphically)
        clone.copyFrom(source);

        // Clona endereços (mantém a regra de pelo menos um)
        if (source.getEnderecos() != null && !source.getEnderecos().isEmpty()) {
            // Clear the default address created by createCliente to have an exact clone of addresses
            clone.getEnderecos().clear();
            for (Endereco end : source.getEnderecos()) {
                clone.addEndereco(enderecoFactory.cloneEndereco(end));
            }
        }
        // else: createCliente already added one default address

        return clone;
    }
}