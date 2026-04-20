package com.pillchill.migration.repository.impl;

import com.pillchill.migration.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public class Template {
    protected <R> R execute(Function<EntityManager, R> function) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try{
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        }
        catch (Exception ex) {
            if(tx!=null&& tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(ex);
        }
        finally {
            if(em!=null) {
                em.close();
            }
        }
    }
}
