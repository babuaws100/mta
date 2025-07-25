package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean(name = "usDataSource")
    public DataSource usDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/us_db");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    @Bean(name = "ukDataSource")
    public DataSource ukDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/uk_db");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    private LocalContainerEntityManagerFactoryBean buildEntityManagerFactory(DataSource ds, String unitName) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setPackagesToScan("com.example.entity");
        emf.setPersistenceUnitName(unitName);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);

        emf.setJpaPropertyMap(props);
        return emf;
    }


    @Bean(name = "usEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean usEntityManagerFactory(
            @Qualifier("usDataSource") DataSource usDataSource) {
        return buildEntityManagerFactory(usDataSource, "us");
    }

    @Bean(name = "ukEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ukEntityManagerFactory(
            @Qualifier("ukDataSource") DataSource ukDataSource) {
        return buildEntityManagerFactory(ukDataSource, "uk");
    }

    @Bean(name = "appDataSource")
    public DataSource dataSource(@Qualifier("usDataSource") DataSource usDs,
                                 @Qualifier("ukDataSource") DataSource ukDs) {
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("us", usDs);
        dataSources.put("uk", ukDs);

        MultiTenantDataSource routingDataSource = new MultiTenantDataSource();
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(dataSources.get("us"));
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("appDataSource") DataSource appDataSource) {
        return buildEntityManagerFactory(appDataSource, "app");
    }


    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}