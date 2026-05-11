package com.desafio.estagio.factory;

import com.desafio.estagio.model.Cliente;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.model.ClienteJuridicoEntity;
import com.desafio.estagio.model.Endereco;
import com.desafio.estagio.model.enums.TipoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClienteFactoryImpl implements ClienteFactory {

    @Autowired
    private EnderecoFactory enderecoFactory;

    @Override
    public Cliente createCliente(TipoCliente tipo) {
        Cliente cliente;

        if (tipo == TipoCliente.FISICA) {
            cliente = new ClienteFisicoEntity();
        } else {
            cliente = new ClienteJuridicoEntity();
        }

        // Cria com um endereço inicial
        Endereco enderecoInicial = enderecoFactory.createEndereco();
        cliente.addEndereco(enderecoInicial);

        return cliente;
    }

    @Override
    public Cliente createClienteFisico() {
        ClienteFisicoEntity cliente = new ClienteFisicoEntity();

        // Cria com um endereço inicial
        Endereco enderecoInicial = enderecoFactory.createEndereco();
        cliente.addEndereco(enderecoInicial);

        return cliente;
    }

    @Override
    public Cliente createClienteJuridico() {
        ClienteJuridicoEntity cliente = new ClienteJuridicoEntity();

        // Cria com um endereço inicial
        Endereco enderecoInicial = enderecoFactory.createEndereco();
        cliente.addEndereco(enderecoInicial);

        return cliente;
    }

    @Override
    public Cliente cloneCliente(Cliente source) {
        if (source == null) return null;

        Cliente clone = createCliente(source.getTipo());

        // Atributos comuns
        clone.setEmail(source.getEmail());
        clone.setEstaAtivo(source.getEstaAtivo());

        // Atributos específicos (com casting)
        if (source.getTipo() == TipoCliente.FISICA) {
            ClienteFisicoEntity sourceFisico = (ClienteFisicoEntity) source;
            ClienteFisicoEntity cloneFisico = (ClienteFisicoEntity) clone;

            cloneFisico.setNome(sourceFisico.getNome());
            cloneFisico.setCpf(sourceFisico.getCpf());
            cloneFisico.setRg(sourceFisico.getRg());
            cloneFisico.setDataNascimento(sourceFisico.getDataNascimento());
        } else {
            ClienteJuridicoEntity sourceJuridico = (ClienteJuridicoEntity) source;
            ClienteJuridicoEntity cloneJuridico = (ClienteJuridicoEntity) clone;

            cloneJuridico.setRazaoSocial(sourceJuridico.getRazaoSocial());
            cloneJuridico.setCnpj(sourceJuridico.getCnpj());
            cloneJuridico.setInscricaoEstadual(sourceJuridico.getInscricaoEstadual());
        }

        // Clona endereços (mantém a regra de pelo menos um)
        if (source.getEnderecos() != null && !source.getEnderecos().isEmpty()) {
            for (Endereco end : source.getEnderecos()) {
                clone.addEndereco(enderecoFactory.cloneEndereco(end));
            }
        } else {
            // Garante que o clone tenha pelo menos um endereço
            clone.addEndereco(enderecoFactory.createEndereco());
        }

        return clone;
    }
}