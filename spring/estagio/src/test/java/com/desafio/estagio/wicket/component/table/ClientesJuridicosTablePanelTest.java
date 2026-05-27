package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.data.DataView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ClientesJuridicosTablePanelTest extends WicketTestBase {

    @Mock
    private ClienteJuridicoService clienteJuridicoService;

    @Mock
    private FileService fileService;

    private TablePanelTestPage page;

    @BeforeEach
    void setUpMocks() {
        when(applicationContext.getBean(ClienteJuridicoService.class)).thenReturn(clienteJuridicoService);
        when(applicationContext.getBean(FileService.class)).thenReturn(fileService);

        Page<ClienteJuridicoListResponse> emptyPage = new PageImpl<>(List.of());
        when(clienteJuridicoService.findAll(any(Pageable.class))).thenReturn(emptyPage);
        when(clienteJuridicoService.count()).thenReturn(0L);

        page = new TablePanelTestPage(new ClientesJuridicosTablePanel("panel"));
    }

    @Test
    @DisplayName("panel renders without errors")
    void rendersPanel() {
        tester.startPage(page);
        tester.assertRenderedPage(TablePanelTestPage.class);
        tester.assertNoErrorMessage();
    }

    @Test
    @DisplayName("panel contains data view for rows")
    void containsDataView() {
        tester.startPage(page);
        tester.assertComponent("panel:tableContainer", WebMarkupContainer.class);
        tester.assertComponent("panel:tableContainer:rows", DataView.class);
    }

    @Test
    @DisplayName("panel contains search form with field and clear button")
    void containsSearchForm() {
        tester.startPage(page);
        tester.assertComponent("panel:searchForm", Form.class);
        tester.assertComponent("panel:searchForm:searchField", TextField.class);
        tester.assertComponent("panel:searchForm:clearSearchBtn", AjaxLink.class);
    }

    @Test
    @DisplayName("panel contains pagination navigator")
    void containsPaginationNavigator() {
        tester.startPage(page);
        tester.assertComponent("panel:navigator", AjaxPagingNavigator.class);
    }
}
