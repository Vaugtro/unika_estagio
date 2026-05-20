package com.desafio.estagio.wicket.page.base;

import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebPage;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new DebugBar("debug"));
    }
}
