package com.desafio.estagio.config;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
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
    public Map<String, JasperReport> jasperReports() throws IOException {
        Map<String, JasperReport> reports = new HashMap<>();

        // Compile all .jrxml files from classpath
        Resource[] resources = resourceResolver.getResources("classpath:reports/*.jrxml");

        if (resources.length == 0) {
            logger.warn("No .jrxml files found in classpath:reports/ directory");
            return reports;
        }

        for (Resource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                String reportName = Objects.requireNonNull(resource.getFilename()).replace(".jrxml", "");
                JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
                reports.put(reportName, jasperReport);
                logger.info("Successfully compiled report: {}", reportName);
            } catch (Exception e) {
                logger.error("Failed to compile report: {}", resource.getFilename(), e);
            }
        }

        logger.info("Total reports compiled: {}", reports.size());
        return reports;
    }
}