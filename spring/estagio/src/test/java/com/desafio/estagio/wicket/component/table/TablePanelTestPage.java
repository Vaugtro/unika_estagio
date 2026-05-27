package com.desafio.estagio.wicket.component.table;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

/** Test page that wraps a component with wicket:id "panel" for rendering in WicketTester. */
public class TablePanelTestPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public TablePanelTestPage(Component panel) {
        add(panel);
    }
}
