package com.pillchill.migration;

import com.pillchill.migration.db.JPAUtil;
import jakarta.persistence.EntityManager;

public class CreateDBSchema {
    public static void main(String[] args) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();
        }
        JPAUtil.shutdown();
    }
}
