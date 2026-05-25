package com.desafio.estagio.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@ComponentScan(
        basePackages = "com.desafio.estagio",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.desafio\\.estagio\\.wicket\\..*"
        )
)
public class TestConfig {
}
