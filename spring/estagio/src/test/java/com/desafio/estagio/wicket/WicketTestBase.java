package com.desafio.estagio.wicket;

import java.lang.reflect.Field;

import com.desafio.estagio.wicket.page.home.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Base class for Wicket tests.
 *
 * Creates a WicketTester backed by a minimal WebApplication that does NOT use
 * Spring injection. Instead, a ComponentInstantiationListener injects
 * Mockito mocks into any field annotated with @SpringBean. This avoids the
 * complexity of mocking the full Spring bean resolution chain.
 */
public class WicketTestBase {

    protected WicketTester tester;

    @BeforeEach
    void setUp() {
        WebApplication app = new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return HomePage.class;
            }

            @Override
            protected void init() {
                super.init();

                getMarkupSettings().setStripWicketTags(false);
                getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

                mockSpringBeanFields();
            }

            private void mockSpringBeanFields() {
                getComponentInstantiationListeners().add(component -> {
                    Class<?> clazz = component.getClass();
                    while (clazz != null && clazz != Object.class) {
                        for (Field field : clazz.getDeclaredFields()) {
                            if (field.isAnnotationPresent(SpringBean.class)) {
                                field.setAccessible(true);
                                try {
                                    Object mockBean = mock(field.getType(),
                                            withSettings().serializable());
                                    field.set(component, mockBean);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(
                                            "Failed to inject mock @SpringBean into " + clazz.getName(), e);
                                }
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                });
            }
        };

        tester = new WicketTester(app);
    }
}
