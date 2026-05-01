package com.pillchill.migration.repository.impl;

import com.pillchill.migration.entity.NhaSanXuat;
import com.pillchill.migration.repository.INhaSanXuatRepository;

import java.util.List;

public class NhaSanXuatRepository extends AbstracGenericRepository<NhaSanXuat, String> implements INhaSanXuatRepository {
    
    public NhaSanXuatRepository() {
        super(NhaSanXuat.class);
    }

    @Override
    public List<NhaSanXuat> findAllActive() {
        return doInTransaction(em -> 
            em.createQuery("SELECT n FROM NhaSanXuat n WHERE n.isActive = true", NhaSanXuat.class)
              .getResultList()
        );
    }
}
