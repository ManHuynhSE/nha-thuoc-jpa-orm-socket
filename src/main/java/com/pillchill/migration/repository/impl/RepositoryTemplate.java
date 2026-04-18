package com.pillchill.migration.repository.impl;

import com.pillchill.migration.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public class RepositoryTemplate {
    protected <R> R execute(Function<EntityManager, R> function) {
        EntityTransaction tx = null;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
