package com.desafio.estagio.wicket.page;

import com.desafio.estagio.wicket.component.table.ClientesFisicosTablePanel;
import com.desafio.estagio.wicket.component.table.ClientesJuridicosTablePanel;
import com.desafio.estagio.wicket.page.abs.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.io.Serial;

public class HomePage extends BasePage {

    private static final String CONTAINER_ID = "currentPanel";
    private final WebMarkupContainer panelContainer;

    public HomePage() {
        super();

        // Create the container FIRST
        panelContainer = new WebMarkupContainer("panelContainer");
        panelContainer.setOutputMarkupId(true);
        add(panelContainer);

        // Add default panel
        showFisicosPanel(null);

        // Then add buttons
        Component BtnFisicos = new BtnFisicos("btnFisicos");
        add(BtnFisicos);

        Component BtnJuridicos = new BtnJuridicos("btnJuridicos");
        add(BtnJuridicos);
    }

    class BtnFisicos extends AjaxLink<Void> {
        @Serial
        private static final long serialVersionUID = 1L;
        public BtnFisicos(String id) {
            super("btnFisicos");
        }
        @Override
        public void onClick(AjaxRequestTarget target) {
            showFisicosPanel(target);
        }
    }

    class BtnJuridicos extends AjaxLink<Void> {
        @Serial
        private static final long serialVersionUID = 1L;
        public BtnJuridicos(String id) {
            super("btnJuridicos");
        }
        @Override
        public void onClick(AjaxRequestTarget target) {
            showJuridicosPanel(target);
        }
    }


    private void showFisicosPanel(AjaxRequestTarget target) {
        ClientesFisicosTablePanel panel = new ClientesFisicosTablePanel(CONTAINER_ID);
        panel.setOutputMarkupId(true);
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }

    private void showJuridicosPanel(AjaxRequestTarget target) {
        ClientesJuridicosTablePanel panel = new ClientesJuridicosTablePanel(CONTAINER_ID);
        panel.setOutputMarkupId(true);
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }
}