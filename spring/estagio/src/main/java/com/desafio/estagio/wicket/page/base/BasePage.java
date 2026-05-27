package com.desafio.estagio.wicket.page.base;

import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import wicket.js.WicketJsAnchor;

public abstract class BasePage extends WebPage {

    private static final ResourceReference BASE_JS = new JavaScriptResourceReference(WicketJsAnchor.class, "base.js");

    public BasePage() {
        add(new DebugBar("debug"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(BASE_JS));
    }
}
