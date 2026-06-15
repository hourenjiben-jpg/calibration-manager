package com.example.calibrationmanager.service;

import com.example.calibrationmanager.model.Tenant;
import com.example.calibrationmanager.repository.TenantRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public Tenant createAndSaveTenant(String code, String name) {
        Tenant tenant = new Tenant();
        tenant.setTenantCode(code);
        tenant.setName(name);

        return tenantRepository.save(tenant);
    }
    
}
