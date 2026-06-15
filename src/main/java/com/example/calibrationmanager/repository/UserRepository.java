package com.example.calibrationmanager.repository;

import com.example.calibrationmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByUsername(String username);

    Optional<User> findByTenant_TenantCodeAndUsername(String tenantCode, String username);

    List<User> findByTenant_Id(Long tenantId);
}
