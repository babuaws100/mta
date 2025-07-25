package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
public class SchemaInitializer {
    @Autowired
    @Qualifier("usEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean usEMF;

    @Autowired
    @Qualifier("ukEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean ukEMF;

    @PostConstruct
    public void init() {
        usEMF.getObject();
        ukEMF.getObject();
    }
}

