package com.example.calibrationmanager.repository;

import com.example.calibrationmanager.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsByTenantCode(String tenantCode);
    
}
