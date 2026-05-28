package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.wicket.WicketTestBase;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClientesFisicosTablePanelTest extends WicketTestBase {

    @Test
    @DisplayName("Panel renders without errors")
    void panelRendersWithoutErrors() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertNoErrorMessage();
    }

    @Test
    @DisplayName("Data view exists inside table container")
    void dataViewExists() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertComponent("panel:tableContainer", WebMarkupContainer.class);
        tester.assertComponent("panel:tableContainer:rows", ClienteFisicoDataView.class);
    }

    @Test
    @DisplayName("Search form renders with search field and clear button")
    void searchFormRenders() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertComponent("panel:searchForm", Form.class);
        tester.assertComponent("panel:searchForm:searchField", TextField.class);
        tester.assertComponent("panel:searchForm:clearSearchBtn", AjaxLink.class);
    }

    @Test
    @DisplayName("Pagination navigator renders")
    void navigatorRenders() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertComponent("panel:navigator", AjaxPagingNavigator.class);
    }

    @Test
    @DisplayName("Modal components render")
    void modalComponentsRender() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertComponent("panel:exportModal", ExportModal.class);
        tester.assertComponent("panel:importModal", ImportModal.class);
        tester.assertComponent("panel:createModal", ClienteFisicoCreateModal.class);
    }

    @Test
    @DisplayName("Edit modal container renders and is visible")
    void editModalContainerRenders() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertComponent("panel:editModalContainer", WebMarkupContainer.class);
        tester.assertVisible("panel:editModalContainer");
    }

    @Test
    @DisplayName("Table container is visible after render")
    void tableContainerIsVisible() {
        var panel = new ClientesFisicosTablePanel("panel");
        tester.startComponentInPage(panel);
        tester.assertVisible("panel:tableContainer");
    }
}
