package com.monarchsolutions.sms.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LegacyDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.legacy")
    public DataSourceProperties legacyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "legacyDataSource")
    public DataSource legacyDataSource(
            @Qualifier("legacyDataSourceProperties") DataSourceProperties legacyDataSourceProperties
    ) {
        return legacyDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "legacyJdbcTemplate")
    public JdbcTemplate legacyJdbcTemplate(@Qualifier("legacyDataSource") DataSource legacyDataSource) {
        return new JdbcTemplate(legacyDataSource);
    }
}
