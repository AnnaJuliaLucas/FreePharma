package com.annaehugo.freepharma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void clearHibernateCache() {
        if (entityManager.getEntityManagerFactory().getCache() != null) {
            entityManager.getEntityManagerFactory().getCache().evictAll();
        }
    }
}