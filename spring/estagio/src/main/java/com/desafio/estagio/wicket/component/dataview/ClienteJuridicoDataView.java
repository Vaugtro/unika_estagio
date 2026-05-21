package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.wicket.component.form.ClienteJuridicoRowUpdateForm;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.io.Serial;

public class ClienteJuridicoDataView extends AbstractClienteDataView<ClienteJuridicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteJuridicoDataView(String id, IDataProvider<ClienteJuridicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateRow(Item<ClienteJuridicoListResponse> item) {
        ClienteJuridicoListResponse cliente = item.getModelObject();
        item.add(new ClienteJuridicoRowUpdateForm("editarForm", cliente, item));
    }
}
