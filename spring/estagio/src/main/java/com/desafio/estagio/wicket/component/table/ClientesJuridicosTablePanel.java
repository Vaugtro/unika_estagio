package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.dataview.ClienteJuridicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteJuridicoCreateModal;
import com.desafio.estagio.wicket.provider.ClienteJuridicoDataProvider;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClientesJuridicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        IDataProvider<ClienteJuridicoListResponse> dataProvider = new ClienteJuridicoDataProvider(clienteJuridicoService);

        DataView<ClienteJuridicoListResponse> dataView = new ClienteJuridicoDataView("rows", dataProvider, 10);

        tableContainer.add(dataView);
        add(new PagingNavigator("navigator", dataView));
        add(new ClienteJuridicoCreateModal("createModal"));
    }
}
