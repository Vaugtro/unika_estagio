package com.desafio.estagio.config;

import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WicketConfig {

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilterRegistration() {
        FilterRegistrationBean<WicketFilter> registration = new FilterRegistrationBean<>();

        WicketFilter wicketFilter = new WicketFilter();
        registration.setFilter(wicketFilter);

        registration.addInitParameter("applicationClassName",
                "com.desafio.estagio.wicket.WicketApplication");
        registration.addInitParameter("filterMappingUrlPattern", "/*");
        registration.addInitParameter("applicationFactoryClassName",
                "org.apache.wicket.spring.SpringWebApplicationFactory");

        // Exclude API and Swagger from Wicket processing
        registration.addInitParameter("ignorePaths", "/api,/swagger-ui,/v3/api-docs,/swagger-ui.html");

        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 100);

        return registration;
    }
}