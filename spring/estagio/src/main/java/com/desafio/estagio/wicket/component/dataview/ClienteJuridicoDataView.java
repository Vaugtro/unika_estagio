package com.desafio.estagio.wicket.component.dataview;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.wicket.component.form.ClienteJuridicoRowUpdateForm;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.io.Serial;

public class ClienteJuridicoDataView extends DataView<ClienteJuridicoListResponse> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteJuridicoDataView(String id, IDataProvider<ClienteJuridicoListResponse> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
        setOutputMarkupId(true);
    }

    @Override
    protected void populateItem(Item<ClienteJuridicoListResponse> item) {
        item.setOutputMarkupId(true);
        ClienteJuridicoListResponse cliente = item.getModelObject();

        ClienteJuridicoRowUpdateForm form = new ClienteJuridicoRowUpdateForm("editarForm", cliente, item);
        item.add(form);
    }
}
