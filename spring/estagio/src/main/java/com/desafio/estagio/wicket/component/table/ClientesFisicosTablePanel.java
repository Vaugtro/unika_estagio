package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.service.ExportService;
import com.desafio.estagio.wicket.component.dataview.ClienteFisicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteFisicoCreateModal;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.provider.ClienteFisicoDataProvider;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.InputStream;
import java.io.Serial;

public class ClientesFisicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    @SpringBean
    private ExportService exportService;

    private WebMarkupContainer tableContainer;
    private ClienteFisicoDataProvider dataProvider;
    private AjaxPagingNavigator navigator;

    public ClientesFisicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        dataProvider = new ClienteFisicoDataProvider(clienteFisicoService);
        DataView<ClienteFisicoListResponse> dataView = new ClienteFisicoDataView("rows", dataProvider, 10);
        tableContainer.add(dataView);

        navigator = new AjaxPagingNavigator("navigator", dataView);
        navigator.setOutputMarkupId(true);
        add(navigator);

        add(buildSearchForm());

        add(new ExportModal("exportModal", "exportFisicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getPdfData() {
                return exportService.pdfFisicos();
            }

            @Override
            protected String getPdfName() {
                return "clientes-fisicos.pdf";
            }

            @Override
            protected byte[] getXlsxData() {
                return exportService.xlsxFisicos();
            }

            @Override
            protected String getXlsxName() {
                return "clientes-fisicos.xlsx";
            }
        });

        add(new ImportModal("importModal", "importFisicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getTemplateData() {
                return exportService.templateFisicosImport();
            }

            @Override
            protected String getTemplateFileName() {
                return "template-clientes-fisicos.xlsx";
            }

            @Override
            protected int importData(InputStream is) throws Exception {
                return exportService.importFisicos(is);
            }

            @Override
            protected String getSuccessMessage() {
                return "cliente(s) físico(s) importado(s) com sucesso!";
            }
        });

        add(new ClienteFisicoCreateModal("createModal"));
    }

    private Form<Void> buildSearchForm() {
        Form<Void> searchForm = new Form<>("searchForm");
        searchForm.setOutputMarkupId(true);

        TextField<String> searchField = new TextField<>("searchField", Model.of(""));
        searchField.setOutputMarkupId(true);
        searchForm.add(searchField);

        // AJAX behavior invoked by JavaScript debounce — reads input from request
        AbstractDefaultAjaxBehavior ajaxBehavior = new AbstractDefaultAjaxBehavior() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(AjaxRequestTarget target) {
                String q = getComponent().getRequest().getRequestParameters()
                        .getParameterValue("input").toString("");
                searchField.setModelObject(q);
                dataProvider.setSearchQuery(q);
                target.add(tableContainer);
                target.add(navigator);
                target.add(searchForm);
                target.appendJavaScript("lucide.createIcons();");
            }
        };
        searchField.add(ajaxBehavior);

        // JavaScript debounce: fires AJAX call 300ms after the user stops typing
        searchField.add(new Behavior() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                String mid = component.getMarkupId();
                String url = ajaxBehavior.getCallbackUrl().toString();
                response.render(OnDomReadyHeaderItem.forScript(
                        "document.getElementById('" + mid + "').addEventListener('input',function(){"
                        + "clearTimeout(this._st);var e=this;this._st=setTimeout(function(){"
                        + "Wicket.Ajax.ajax({'u':'" + url + "','ep':{'input':e.value}})"
                        + "},300);});"
                ));
            }
        });

        searchForm.add(new AjaxLink<Void>("clearSearchBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                searchField.setModelObject("");
                dataProvider.setSearchQuery(null);
                target.add(tableContainer);
                target.add(navigator);
                target.add(searchForm);
                target.appendJavaScript("lucide.createIcons();");
            }
        });

        return searchForm;
    }


}
