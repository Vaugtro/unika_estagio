package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.page.home.HomePage;
import org.junit.jupiter.api.Test;

class HomePageSmokeTest extends WicketTestBase {

    @Test
    void rendersHomePage() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.assertNoErrorMessage();
    }
}
