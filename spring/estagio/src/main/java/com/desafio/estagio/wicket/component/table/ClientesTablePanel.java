package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.dto.AbstractClienteListResponse;
import com.desafio.estagio.wicket.builder.ComponentAttributeBuilder;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.provider.AbstractClienteDataProvider;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
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
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.Model;

import java.io.InputStream;
import java.io.Serial;

/**
 * Unified generic table panel for client listing (fisico/juridico).
 * <p>
 * Encapsulates shared UI: search form with debounce, pagination,
 * export/import modals, create modal, and table refresh.
 * <p>
 * Concrete subclasses implement abstract methods for type-specific behavior
 * (data provider, data view, modal creation, file export/import).
 *
 * @param <T> the list-response DTO type, must extend {@link AbstractClienteListResponse}
 */
public abstract class ClientesTablePanel<T extends AbstractClienteListResponse> extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final long ITEMS_PER_PAGE = 10;

    private WebMarkupContainer tableContainer;
    private WebMarkupContainer editModalContainer;
    protected AbstractClienteDataProvider<T> dataProvider;
    private AjaxPagingNavigator navigator;

    public ClientesTablePanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // --- Table container ---
        tableContainer = new WebMarkupContainer("tableContainer");
        ComponentAttributeBuilder.of(tableContainer).setOutputMarkupId(true).build();
        add(tableContainer);

        // --- Data provider + data view (type-specific) ---
        dataProvider = createDataProvider();
        DataView<T> dataView = createDataView("rows", dataProvider);
        tableContainer.add(dataView);

        // --- Edit modal container (placeholder) ---
        editModalContainer = new WebMarkupContainer("editModalContainer");
        ComponentAttributeBuilder.of(editModalContainer).setOutputMarkupId(true).build();
        editModalContainer.add(new WebMarkupContainer("editModal").setVisible(false));
        add(editModalContainer);

        // --- Pagination navigator ---
        navigator = new AjaxPagingNavigator("navigator", dataView) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onAjaxEvent(AjaxRequestTarget target) {
                super.onAjaxEvent(target);
                target.add(this);
                JavaScriptUtils.reloadLucideIcons(target);
            }
        };
        ComponentAttributeBuilder.of(navigator).setOutputMarkupId(true).build();
        add(navigator);

        // --- Search form ---
        add(buildSearchForm());

        // --- Export modal ---
        add(buildExportModal());

        // --- Import modal ---
        add(buildImportModal());

        // --- Create modal (type-specific) ---
        add(createCreateModal("createModal"));
    }

    /**
     * Builds the search form with debounced AJAX input and clear button.
     */
    private Form<Void> buildSearchForm() {
        Form<Void> searchForm = new Form<>("searchForm");
        ComponentAttributeBuilder.of(searchForm).setOutputMarkupId(true).build();

        TextField<String> searchField = new TextField<>("searchField", Model.of(""));
        ComponentAttributeBuilder.of(searchField).setOutputMarkupId(true).build();
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
                JavaScriptUtils.reloadLucideIcons(target);
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

        // Clear search button
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
                JavaScriptUtils.reloadLucideIcons(target);
            }
        });

        return searchForm;
    }

    /**
     * Builds the export modal delegating to type-specific abstract methods.
     */
    private ExportModal buildExportModal() {
        return new ExportModal("exportModal", getExportModalWindowId()) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getPdfData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? supplyPdfData(q)
                        : supplyPdfData(null);
            }

            @Override
            protected String getPdfName() {
                return getPdfFileName();
            }

            @Override
            protected byte[] getXlsxData() {
                String q = dataProvider.getSearchQuery();
                return (q != null && !q.isBlank())
                        ? supplyXlsxData(q)
                        : supplyXlsxData(null);
            }

            @Override
            protected String getXlsxName() {
                return getXlsxFileName();
            }
        };
    }

    /**
     * Builds the import modal delegating to type-specific abstract methods.
     */
    private ImportModal buildImportModal() {
        return new ImportModal("importModal", getImportModalWindowId()) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getTemplateData() {
                return supplyImportTemplate();
            }

            @Override
            protected String getTemplateFileName() {
                return getImportTemplateFileName();
            }

            @Override
            protected int importData(InputStream is) throws Exception {
                return processImportData(is);
            }

            @Override
            protected String getSuccessMessage() {
                return getImportSuccessMessage();
            }
        };
    }

    // ========================================================================
    // Abstract methods — type-specific behavior
    // ========================================================================

    /**
     * Creates the type-specific data provider (e.g. ClienteFisicoDataProvider).
     */
    protected abstract AbstractClienteDataProvider<T> createDataProvider();

    /**
     * Creates the type-specific data view (e.g. ClienteFisicoDataView).
     */
    protected abstract DataView<T> createDataView(String id, IDataProvider<T> provider);

    /**
     * Creates the type-specific create modal (e.g. ClienteFisicoCreateModal).
     */
    protected abstract Component createCreateModal(String id);

    // --- Export ---

    /**
     * Supplies PDF bytes for export.
     *
     * @param query search filter, or {@code null} for unfiltered export
     */
    protected abstract byte[] supplyPdfData(String query);

    /**
     * Supplies XLSX bytes for export.
     *
     * @param query search filter, or {@code null} for unfiltered export
     */
    protected abstract byte[] supplyXlsxData(String query);

    /** File name for the exported PDF (e.g. "clientes-fisicos.pdf"). */
    protected abstract String getPdfFileName();

    /** File name for the exported XLSX (e.g. "clientes-fisicos.xlsx"). */
    protected abstract String getXlsxFileName();

    /**
     * HTML {@code id} attribute for the export modal root element
     * (e.g. {@code "exportFisicoModal"}).
     */
    protected abstract String getExportModalWindowId();

    // --- Import ---

    /** Supplies the import template XLSX bytes. */
    protected abstract byte[] supplyImportTemplate();

    /** File name for the downloadable import template. */
    protected abstract String getImportTemplateFileName();

    /**
     * Processes an uploaded import file.
     *
     * @param is input stream of the uploaded XLSX
     * @return number of records imported
     */
    protected abstract int processImportData(InputStream is) throws Exception;

    /** Success message suffix (e.g. "cliente(s) físico(s) importado(s) com sucesso!"). */
    protected abstract String getImportSuccessMessage();

    /**
     * HTML {@code id} attribute for the import modal root element
     * (e.g. {@code "importFisicoModal"}).
     */
    protected abstract String getImportModalWindowId();

    // ========================================================================
    // Public API
    // ========================================================================

    public WebMarkupContainer getEditModalContainer() {
        return editModalContainer;
    }

    /**
     * Refreshes the table container and paginator, reinitializing Lucide icons
     * and input masks.
     */
    public void refreshTable(AjaxRequestTarget target) {
        target.add(tableContainer);
        target.add(navigator);
        JavaScriptUtils.reinitializeMasksSafe(target);
        JavaScriptUtils.reloadLucideIconsSafe(target);
    }
}
