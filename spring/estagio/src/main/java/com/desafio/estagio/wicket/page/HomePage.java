package com.desafio.estagio.wicket.page;

import com.desafio.estagio.wicket.component.table.ClientesFisicosTablePanel;
import com.desafio.estagio.wicket.component.table.ClientesJuridicosTablePanel;
import com.desafio.estagio.wicket.page.abs.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class HomePage extends BasePage {

    private final WebMarkupContainer panelContainer;

    public HomePage() {
        // Create container with FIXED ID
        panelContainer = new WebMarkupContainer("panelContainer");
        panelContainer.setOutputMarkupId(true);
        panelContainer.setMarkupId("panelContainer");
        add(panelContainer);

        showFisicosPanel(null);

        add(new AjaxLink<Void>("btnFisicos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showFisicosPanel(target);
            }
        });
        /*
        // Button for Jurídicos
        add(new AjaxLink<Void>("btnJuridicos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showJuridicosPanel(target);
            }
        });*/
    }

    private void showFisicosPanel(AjaxRequestTarget target) {
        ClientesFisicosTablePanel panel = new ClientesFisicosTablePanel("currentPanel");
        panel.setOutputMarkupId(true);
        panel.setMarkupId("currentPanel");
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }
    /*
    private void showJuridicosPanel(AjaxRequestTarget target) {
        ClientesJuridicosTablePanel panel = new ClientesJuridicosTablePanel("currentPanel");
        panel.setOutputMarkupId(true);
        panel.setMarkupId("currentPanel");
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }*/
}