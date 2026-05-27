package com.desafio.estagio.wicket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.desafio.estagio.wicket.page.home.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.devutils.debugbar.DebugBar;
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
 *
 * Subclasses can configure specific mocks via {@link #configureMock(Class, Object)}
 * before calling {@code super.setUp()} to have custom mock behavior injected
 * into {@code @SpringBean} fields.
 */
public class WicketTestBase {

    protected WicketTester tester;
    private final Map<Class<?>, Object> configuredMocks = new HashMap<>();

    /**
     * Registers a pre-configured mock to be injected into any {@code @SpringBean}
     * field of the given type. Call this before {@code super.setUp()} in subclasses
     * that override the {@code setUp()} method.
     */
    protected <T> void configureMock(Class<T> type, T mock) {
        configuredMocks.put(type, mock);
    }

    @BeforeEach
    protected void setUp() {
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

                // DebugBar contributors fail to render in test environment
                // (Label inside contributor 3 has no model). Clear them here
                // so the DebugBar renders without error.
                DebugBar.setContributors(new ArrayList<>(), this);

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
                                    Object mockBean = configuredMocks.getOrDefault(
                                            field.getType(),
                                            mock(field.getType(), withSettings().serializable())
                                    );
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
