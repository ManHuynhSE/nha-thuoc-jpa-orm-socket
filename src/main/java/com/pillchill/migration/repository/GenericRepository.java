package com.pillchill.migration.repository;

import java.util.List;

public interface GenericRepository<T,ID>{
    T create(T t);
    T update(T t);
    boolean delete(ID id);
    T findByID(ID id);
    List<T> loadAll();
}
