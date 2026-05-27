package com.desafio.estagio.wicket.component.table;

import com.desafio.estagio.service.FileService;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

/**
 * Generic base panel for clientes table views (Fisico / Juridico).
 * <p>
 * Subclasses implement the abstract factory methods to provide type-specific
 * components (data provider, data view, modals) and search query delegation.
 *
 * @param <T> the list response DTO type (e.g. {@code ClienteFisicoListResponse})
 */
public abstract class ClientesTablePanel<T> extends DevUtilsPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    protected FileService fileService;

    private WebMarkupContainer tableContainer;
    private WebMarkupContainer editModalContainer;
    private AjaxPagingNavigator navigator;

    public ClientesTablePanel(String id) {
        super(id);
    }

    // -----------------------------------------------------------
    // Abstract factory methods — subclasses provide concrete types
    // -----------------------------------------------------------

    protected abstract AbstractClienteDataProvider<T> createDataProvider();

    protected abstract DataView<T> createDataView(String id, IDataProvider<T> provider, long itemsPerPage);

    protected abstract Component createCreateModal(String id);

    protected abstract ExportModal createExportModal(String id);

    protected abstract ImportModal createImportModal(String id);

    // -----------------------------------------------------------
    // Search query — subclasses delegate to their concrete provider
    // -----------------------------------------------------------

    protected abstract String getSearchQuery();

    protected abstract void setSearchQuery(String query);

    // -----------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------

    @Override
    protected void onInitialize() {
        super.onInitialize();

        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        AbstractClienteDataProvider<T> dataProvider = createDataProvider();
        DataView<T> dataView = createDataView("rows", dataProvider, 10);
        tableContainer.add(dataView);

        editModalContainer = new WebMarkupContainer("editModalContainer");
        editModalContainer.setOutputMarkupId(true);
        editModalContainer.add(new WebMarkupContainer("editModal").setVisible(false));
        add(editModalContainer);

        navigator = new AjaxPagingNavigator("navigator", dataView) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onAjaxEvent(AjaxRequestTarget target) {
                super.onAjaxEvent(target);
                target.add(this);
                JavaScriptUtils.createIcons(target);
            }
        };
        navigator.setOutputMarkupId(true);
        add(navigator);

        add(buildSearchForm());
        add(createExportModal("exportModal"));
        add(createImportModal("importModal"));
        add(createCreateModal("createModal"));
    }

    // -----------------------------------------------------------
    // Search form (identical between Fisico / Juridico)
    // -----------------------------------------------------------

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
                setSearchQuery(q);
                target.add(tableContainer);
                target.add(navigator);
                JavaScriptUtils.createIcons(target);
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
                setSearchQuery(null);
                target.add(tableContainer);
                target.add(navigator);
                target.add(searchForm);
                JavaScriptUtils.createIcons(target);
            }
        });

        return searchForm;
    }

    // -----------------------------------------------------------
    // Public API
    // -----------------------------------------------------------

    public WebMarkupContainer getEditModalContainer() {
        return editModalContainer;
    }

    public void refreshTable(AjaxRequestTarget target) {
        target.add(tableContainer);
        target.add(navigator);
        JavaScriptUtils.createIconsSafe(target);
        JavaScriptUtils.reapplyMasksSafe(target);
    }
}
