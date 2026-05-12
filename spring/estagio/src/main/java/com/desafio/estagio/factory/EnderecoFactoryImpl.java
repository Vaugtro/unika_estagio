package com.desafio.estagio.factory;

import com.desafio.estagio.model.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoFactoryImpl implements EnderecoFactory {

    @Override
    public Endereco createEndereco() {
        Endereco endereco = new Endereco();
        endereco.setPrincipal(false);
        return endereco;
    }

    @Override
    public Endereco cloneEndereco(Endereco source) {
        if (source == null) return null;

        Endereco clone = createEndereco();
        clone.setLogradouro(source.getLogradouro());
        clone.setNumero(source.getNumero());
        clone.setComplemento(source.getComplemento());
        clone.setCep(source.getCep());
        clone.setBairro(source.getBairro());
        clone.setCidade(source.getCidade());
        clone.setEstado(source.getEstado());
        clone.setTelefone(source.getTelefone());
        clone.setPrincipal(source.getPrincipal());

        return clone;
    }
}