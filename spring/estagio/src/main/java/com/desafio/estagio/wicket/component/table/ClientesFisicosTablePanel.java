package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
import com.desafio.estagio.wicket.provider.ClienteFisicoDataProvider;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class ClientesFisicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        IDataProvider<ClienteFisicoListResponse> dataProvider = new ClienteFisicoDataProvider(clienteFisicoService);

        DataView<ClienteFisicoListResponse> dataView = new ClienteFisicoDataView("rows", dataProvider, 10);

        tableContainer.add(dataView);
        add(new ClienteFisicoCreateModal("createModal"));
    }
}