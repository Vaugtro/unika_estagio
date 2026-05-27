package com.desafio.estagio.wicket;

import com.desafio.estagio.wicket.page.home.HomePage;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for HomePage.
 *
 * Verifies that the page renders without errors using a mocked
 * Spring context. Any @SpringBean dependencies are satisfied by
 * Mockito mocks returned from the mocked ApplicationContext.
 */
class HomePageSmokeTest extends WicketTestBase {

    @Test
    void homePageRendersWithoutErrors() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.assertNoErrorMessage();
    }
}
