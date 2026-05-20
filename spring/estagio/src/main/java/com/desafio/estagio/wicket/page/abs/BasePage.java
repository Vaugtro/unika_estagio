package com.desafio.estagio.wicket.page.abs;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.devutils.debugbar.DebugBar;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new DebugBar("debug"));
    }
}
