package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoListResponse;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.wicket.component.dataview.ClienteJuridicoDataView;
import com.desafio.estagio.wicket.component.modal.ClienteJuridicoCreateModal;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.provider.ClienteJuridicoDataProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
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

public class ClientesJuridicosTablePanel extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    @SpringBean
    private FileService fileService;

    private WebMarkupContainer tableContainer;
    private ClienteJuridicoDataProvider dataProvider;
    private AjaxPagingNavigator navigator;

    public ClientesJuridicosTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        dataProvider = new ClienteJuridicoDataProvider(clienteJuridicoService);
        DataView<ClienteJuridicoListResponse> dataView = new ClienteJuridicoDataView("rows", dataProvider, 10);
        tableContainer.add(dataView);

        navigator = new AjaxPagingNavigator("navigator", dataView) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onAjaxEvent(AjaxRequestTarget target) {
                super.onAjaxEvent(target);
                target.add(this);
                target.appendJavaScript("lucide.createIcons();");
            }
        };
        navigator.setOutputMarkupId(true);
        add(navigator);

        add(buildSearchForm());

        add(new ExportModal("exportModal", "exportJuridicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getPdfData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? fileService.pdfJuridicosPorFiltro(q)
                        : fileService.pdfJuridicos();
            }

            @Override
            protected String getPdfName() {
                return "clientes-juridicos.pdf";
            }

            @Override
            protected byte[] getXlsxData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? fileService.xlsxJuridicosPorFiltro(q)
                        : fileService.xlsxJuridicos();
            }

            @Override
            protected String getXlsxName() {
                return "clientes-juridicos.xlsx";
            }
        });

        add(new ImportModal("importModal", "importJuridicoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getTemplateData() {
                return fileService.templateJuridicosImport();
            }

            @Override
            protected String getTemplateFileName() {
                return "template-clientes-juridicos.xlsx";
            }

            @Override
            protected int importData(InputStream is) throws Exception {
                return fileService.importJuridicos(is);
            }

            @Override
            protected String getSuccessMessage() {
                return "cliente(s) jurídico(s) importado(s) com sucesso!";
            }
        });

        add(new ClienteJuridicoCreateModal("createModal"));
    }

    private Form<Void> buildSearchForm() {
        Form<Void> searchForm = new Form<>("searchForm");
        searchForm.setOutputMarkupId(true);

        TextField<String> searchField = new TextField<>("searchField", Model.of(""));
        searchField.setOutputMarkupId(true);
        searchForm.add(searchField);

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
                target.appendJavaScript("lucide.createIcons();");
            }
        };
        searchField.add(ajaxBehavior);

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
