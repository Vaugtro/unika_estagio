package com.desafio.estagio.wicket.component.dataview;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.io.Serial;

public abstract class AbstractClienteDataView<T> extends DataView<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    public AbstractClienteDataView(String id, IDataProvider<T> dataProvider, long itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
        setOutputMarkupId(true);
    }

    @Override
    protected void populateItem(Item<T> item) {
        item.setOutputMarkupId(true);
        populateRow(item);
    }

    protected abstract void populateRow(Item<T> item);
}
