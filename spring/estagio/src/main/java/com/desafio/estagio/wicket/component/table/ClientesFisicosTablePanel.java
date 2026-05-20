package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.table.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.table.provider.ClienteFisicoDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public class ClientesFisicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private final ClienteFisicoService clienteFisicoService;

    public ClientesFisicosTablePanel(String id, ClienteFisicoService clienteFisicoService) {
        super(id);
        this.clienteFisicoService = clienteFisicoService;
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
    }
}