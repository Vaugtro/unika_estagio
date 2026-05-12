package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.pages.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.objects.checker.CheckingObjectOutputStream;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static org.apache.wicket.ThreadContext.getSession;

@Component
public class WicketApplication extends WebApplication {

    private final ApplicationContext applicationContext;

    public WicketApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void init() {
        super.init();

        // Enable Spring injection
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext)
        );

        // Mount the home page to root
        mountPage("/", HomePage.class);


        getSessionStore().bind(null, null);
        getSession().invalidate();

        // Configure resource settings - IMPORTANT for finding HTML files
        getResourceSettings().setUseDefaultOnMissingResource(true);
        getResourceSettings().setResourcePollFrequency(null);
        getMarkupSettings().setStripWicketTags(false);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getResourceSettings().setResourcePollFrequency(Duration.seconds(2));
        getStoreSettings().setInmemoryCacheSize(0);
        getStoreSettings().setMaxSizePerSession(Bytes.kilobytes(0));

        // Debug
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext)
        );

        getDebugSettings().setDevelopmentUtilitiesEnabled(true);

        getDebugSettings().setAjaxDebugModeEnabled(true);

        getExceptionSettings().setUnexpectedExceptionDisplay(
                ExceptionSettings.SHOW_EXCEPTION_PAGE
        );

        getPageSettings().setVersionPagesByDefault(false);

        getStoreSettings().setInmemoryCacheSize(0);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}