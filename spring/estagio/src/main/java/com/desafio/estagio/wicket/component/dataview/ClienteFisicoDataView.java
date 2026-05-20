package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.form.ClienteFisicoRowUpdateForm;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClienteFisicoDataView extends DataView<ClienteFisicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoDataView(String id, IDataProvider<ClienteFisicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
        setOutputMarkupId(true);
    }

    @Override
    protected void populateItem(Item<ClienteFisicoListResponse> item) {
        item.setOutputMarkupId(true);

        ClienteFisicoListResponse cliente = item.getModelObject();

        ClienteFisicoRowUpdateForm form = new ClienteFisicoRowUpdateForm("editarForm", cliente, item);
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
