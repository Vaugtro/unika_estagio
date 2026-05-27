package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
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

class ClientesFisicosTablePanelTest extends WicketTestBase {

    @Mock
    private ClienteFisicoService clienteFisicoService;

    @Mock
    private FileService fileService;

    private TablePanelTestPage page;

    @BeforeEach
    void setUpMocks() {
        when(applicationContext.getBean(ClienteFisicoService.class)).thenReturn(clienteFisicoService);
        when(applicationContext.getBean(FileService.class)).thenReturn(fileService);

        Page<ClienteFisicoListResponse> emptyPage = new PageImpl<>(List.of());
        when(clienteFisicoService.findAll(any(Pageable.class))).thenReturn(emptyPage);
        when(clienteFisicoService.count()).thenReturn(0L);

        page = new TablePanelTestPage(new ClientesFisicosTablePanel("panel"));
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
