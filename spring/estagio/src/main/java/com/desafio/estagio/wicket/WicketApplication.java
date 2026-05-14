package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.page.HomePage;
import lombok.Getter;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.settings.JavaScriptLibrarySettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WicketApplication extends WebApplication {

    private final ApplicationContext applicationContext;

    public WicketApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void init() {
        super.init();

        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext)
        );

        JavaScriptLibrarySettings jsSettings = getJavaScriptLibrarySettings();

        // Criar referência para seu jQuery 3.7.1
        ResourceReference jqueryReference = new UrlResourceReference(
                org.apache.wicket.request.Url.parse("https://code.jquery.com/jquery-3.7.1.min.js")
        );

        // Set para usar seu jQuery
        jsSettings.setJQueryReference(jqueryReference);

        // IMPORTANTE: Desabilitar a compressão de JavaScript para evitar conflitos
        getResourceSettings().setUseMinifiedResources(false);

        // Configurações adicionais para compatibilidade
        getRequestCycleSettings().setTimeout(Duration.valueOf(60000)); // 60 seconds timeout

        // Mount pages
        mountPage("/", HomePage.class);

        // Resource settings
        getResourceSettings().setUseDefaultOnMissingResource(true);
        getResourceSettings().setResourcePollFrequency(Duration.seconds(2));
        getMarkupSettings().setStripWicketTags(false);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        // Store settings
        //getStoreSettings().setInmemoryCacheSize(0);
        //getStoreSettings().setMaxSizePerSession(Bytes.kilobytes(1024));

        // Debug settings
        getDebugSettings().setDevelopmentUtilitiesEnabled(true);
        getDebugSettings().setAjaxDebugModeEnabled(true);

        // Exception settings
        getExceptionSettings().setUnexpectedExceptionDisplay(
                ExceptionSettings.SHOW_EXCEPTION_PAGE
        );

        // Page settings
        getPageSettings().setVersionPagesByDefault(false);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}