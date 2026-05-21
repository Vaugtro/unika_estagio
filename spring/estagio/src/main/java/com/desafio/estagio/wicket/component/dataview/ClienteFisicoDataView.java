package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.wicket.component.form.ClienteFisicoRowUpdateForm;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.io.Serial;

public class ClienteFisicoDataView extends AbstractClienteDataView<ClienteFisicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteFisicoDataView(String id, IDataProvider<ClienteFisicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateRow(Item<ClienteFisicoListResponse> item) {
        ClienteFisicoListResponse cliente = item.getModelObject();
        item.add(new ClienteFisicoRowUpdateForm("editarForm", cliente, item));
    }
}
