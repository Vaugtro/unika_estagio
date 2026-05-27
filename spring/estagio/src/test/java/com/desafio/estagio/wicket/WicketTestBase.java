package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.application.WicketApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public abstract class WicketTestBase {

    protected WicketTester tester;

    @Mock
    protected ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        tester = new WicketTester(new WicketApplication(applicationContext));
    }
}
