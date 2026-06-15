package com.example.calibrationmanager.repository;

import com.example.calibrationmanager.model.CalibrationEquipment;
import com.example.calibrationmanager.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CalibrationRepository extends JpaRepository < CalibrationEquipment, Long > {

    // 特定のテナントに属する機器だけ取得
    List<CalibrationEquipment> findByTenant(Tenant tenant);
    
}
