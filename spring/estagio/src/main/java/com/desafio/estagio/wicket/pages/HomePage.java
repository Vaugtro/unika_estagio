package com.desafio.estagio.wicket.pages;

import com.desafio.estagio.wicket.components.table.ClientesFisicosPanel;
import com.desafio.estagio.wicket.components.table.ClientesJuridicosPanel;
import com.desafio.estagio.wicket.pages.abs.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends BasePage {

    private WebMarkupContainer panelContainer;

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

        // Button for Jurídicos
        add(new AjaxLink<Void>("btnJuridicos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showJuridicosPanel(target);
            }
        });
    }

    private void showFisicosPanel(AjaxRequestTarget target) {
        ClientesFisicosPanel panel = new ClientesFisicosPanel("currentPanel");
        panel.setOutputMarkupId(true);
        panel.setMarkupId("currentPanel");
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }

    private void showJuridicosPanel(AjaxRequestTarget target) {
        ClientesJuridicosPanel panel = new ClientesJuridicosPanel("currentPanel");
        panel.setOutputMarkupId(true);
        panel.setMarkupId("currentPanel");
        panelContainer.addOrReplace(panel);

        if (target != null) {
            target.add(panelContainer);
            target.appendJavaScript("lucide.createIcons();");
        }
    }
}