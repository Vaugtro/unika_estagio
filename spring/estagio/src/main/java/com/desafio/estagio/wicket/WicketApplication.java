package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.pages.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
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

        // Enable Spring injection
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext)
        );

        // Mount the home page to root
        mountPage("/", HomePage.class);

        // Configure resource settings - IMPORTANT for finding HTML files
        getResourceSettings().setUseDefaultOnMissingResource(true);
        getResourceSettings().setResourcePollFrequency(null);
        getMarkupSettings().setStripWicketTags(false);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getResourceSettings().setResourcePollFrequency(Duration.seconds(2));
        getFrameworkSettings().setSerializer(new JavaSerializer(getApplicationKey()));
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}