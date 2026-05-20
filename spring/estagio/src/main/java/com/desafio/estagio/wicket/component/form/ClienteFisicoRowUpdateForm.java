package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.btn.EditarClienteFisicoBtn;
import com.desafio.estagio.wicket.models.ClienteFisicoModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClienteFisicoRowUpdateForm extends Form<ClienteFisicoModel> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteFisicoRowUpdateForm(String id, ClienteFisicoListResponse cliente) {
        super(id);

        this.setOutputMarkupId(true);

        CompoundPropertyModel<ClienteFisicoModel> model = new CompoundPropertyModel<>(new ClienteFisicoModel(cliente));

        this.setModel(model);

        this.add(new Label("id"));
        this.add(new Label("cpf"));
        this.add(new TextField<>("nome"));
        this.add(new TextField<>("email"));
        this.add(new Label("status", cliente.estaAtivo() ? "Ativo" : "Inativo").add(
                new AttributeModifier("class",
                    cliente.estaAtivo() ? "badge bg-success" : "badge bg-danger"))
        );
        this.add(new EditarClienteFisicoBtn(this));
    }
}
