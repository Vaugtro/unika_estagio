package com.desafio.estagio.wicket.component.table.dataview;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.btn.EditarClienteFisicoBtn;
import com.desafio.estagio.wicket.component.form.ClienteFisicoRowUpdateForm;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serial;

public class ClienteFisicoDataView extends DataView<ClienteFisicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteFisicoDataView(String id, IDataProvider<ClienteFisicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(Item<ClienteFisicoListResponse> item) {

        ClienteFisicoListResponse cliente = item.getModelObject();

        ClienteFisicoRowUpdateForm form = new ClienteFisicoRowUpdateForm("editarForm", cliente);
        item.add(form);

        /*
        // Toggle active/inactive button
        AjaxLink<Void> toggleBtn = new AjaxLink<>("toggleAtivoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    if (cliente.estaAtivo()) {
                        clienteFisicoService.inactivate(cliente.id());
                        success("Cliente inativado com sucesso!");
                    } else {
                        clienteFisicoService.activate(cliente.id());
                        success("Cliente ativado com sucesso!");
                    }
                    target.add(parentElement);
                    target.appendJavaScript("lucide.createIcons();");

                    if (getPage().get("feedback") != null) {
                        target.add(getPage().get("feedback"));
                    }
                } catch (Exception e) {
                    error("Erro ao alterar status: " + e.getMessage());
                    if (getPage().get("feedback") != null) {
                        target.add(getPage().get("feedback"));
                    }
                }
            }
        };
        toggleBtn.add(new AttributeModifier("class",
                cliente.estaAtivo() ? "btn btn-sm btn-success" : "btn btn-sm btn-danger"));
        toggleBtn.add(new AttributeModifier("title",
                cliente.estaAtivo() ? "Inativar" : "Ativar"));
        editarForm.add(toggleBtn);

        item.add(editarForm);*/
    }
}
