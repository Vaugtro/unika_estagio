package com.desafio.estagio.config;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class JasperReportsConfig {

    private static final Logger logger = LoggerFactory.getLogger(JasperReportsConfig.class);

    private final ResourcePatternResolver resourceResolver = ResourcePatternUtils.getResourcePatternResolver(null);

    @Bean
    public Map<String, JasperReport> jasperReports() {
        Map<String, JasperReport> reports = new HashMap<>();

        try {
            Resource[] resources = resourceResolver.getResources("classpath:reports/*.jrxml");
            logger.info("Found {} .jrxml file(s) in classpath:reports/", resources.length);

            for (Resource resource : resources) {
                logger.info("Processing: {}", resource.getFilename());

                try (InputStream inputStream = resource.getInputStream()) {
                    String reportName = Objects.requireNonNull(resource.getFilename()).replace(".jrxml", "");

                    // Load as JasperDesign first
                    JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

                    // Then compile to JasperReport
                    JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

                    reports.put(reportName, jasperReport);
                    logger.info("Successfully loaded and compiled report: {}", reportName);

                } catch (Exception e) {
                    logger.error("Failed to load report: {}", resource.getFilename(), e);
                }
            }

        } catch (IOException e) {
            logger.error("Failed to scan reports directory", e);
        }

        logger.info("Total reports loaded: {}", reports.size());
        return reports;
    }
}