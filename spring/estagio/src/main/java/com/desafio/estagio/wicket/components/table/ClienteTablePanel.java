package com.desafio.estagio.wicket.components.table;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.wicket.components.paginator.LucidePaginator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.markup.repeater.Item;

import java.io.Serializable;
import java.util.List;


public abstract class ClienteTablePanel<T extends Serializable> extends Panel {

    private final LoadableDetachableModel<List<T>> clientesModel;
    private final DataView<T> dataView;

    public ClienteTablePanel(String id) {
        super(id);

        setOutputMarkupId(true);
        setMarkupId(id);

        clientesModel = new LoadableDetachableModel<List<T>>() {
            @Override
            protected List<T> load() {
                return loadClientes();
            }
        };

        List<T> clientes = clientesModel.getObject();

        dataView = new DataView<T>("rows", new ListDataProvider<>(clientes), 10) {
            @Override
            protected void populateItem(Item<T> item) {
                T cliente = item.getModelObject();
                populateRow(item, cliente);
            }
        };

        // Add components to the panel
        add(dataView);
        add(new LucidePaginator("navigator", dataView));
    }

    protected abstract List<T> loadClientes();
    protected abstract void populateRow(Item<T> item, T cliente);

    @Override
    protected void onDetach() {
        clientesModel.detach();
        super.onDetach();
    }
}