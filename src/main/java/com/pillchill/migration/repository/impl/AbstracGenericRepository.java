package com.pillchill.migration.repository.impl;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.repository.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.function.Function;

public abstract class AbstracGenericRepository<T,ID>  implements GenericRepository<T, ID> {

    protected final Class<T> entityClass;

    protected AbstracGenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R doInTransaction(Function<EntityManager,R> function) {
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


    @Override
    public T create(T t) {
        return doInTransaction(em->{
            em.persist(t);
            return t;
        });
    }

    @Override
    public T update(T t) {
        return doInTransaction(em->{
            em.merge(t);
            return t;
        });
    }

    @Override
    public boolean delete(ID id) {
        return doInTransaction(em->{
            T t  = em.find(entityClass,id);
            em.remove(t);
            return true;
        });
    }

    @Override
    public T findByID(ID id) {
        return doInTransaction(em->{
            T t = em.find(entityClass,id);
            return t;
        });
    }

    @Override
    public List<T> loadAll() {
        String query = "FROM " + entityClass.getSimpleName();
        return doInTransaction(em->{
            return em.createQuery(query,entityClass).getResultList();
        });
    }
}
