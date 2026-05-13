package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.page.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WicketApplication extends WebApplication {

    private final ApplicationContext applicationContext;

    public WicketApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void init() {
        super.init();

        // Enable Spring injection (apenas uma vez)
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext)
        );

        // Mount pages
        mountPage("/", HomePage.class);
        // Adicione outros mapeamentos aqui
        // mountPage("/clientes-fisicos", ClientesFisicosPage.class);

        // Resource settings
        getResourceSettings().setUseDefaultOnMissingResource(true);
        getResourceSettings().setResourcePollFrequency(Duration.seconds(2));
        getMarkupSettings().setStripWicketTags(false);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        // Store settings
        getStoreSettings().setInmemoryCacheSize(0);
        getStoreSettings().setMaxSizePerSession(Bytes.kilobytes(1024));

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