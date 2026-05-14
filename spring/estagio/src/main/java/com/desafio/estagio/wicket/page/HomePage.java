package com.desafio.estagio.wicket.page;

import com.desafio.estagio.wicket.component.table.ClientesFisicosTablePanel;
import com.desafio.estagio.wicket.component.table.ClientesJuridicosTablePanel;
import com.desafio.estagio.wicket.page.abs.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.io.Serial;

public class HomePage extends BasePage {

    @Serial
    private static final long serialVersionUID = 1L;
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
        add(new AjaxLink<Void>("btnFisicos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showFisicosPanel(target);
            }
        });

        add(new AjaxLink<Void>("btnJuridicos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showJuridicosPanel(target);
            }
        });
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