package com.example.calibrationmanager.repository;

import com.example.calibrationmanager.model.CalibrationEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalibrationRepository extends JpaRepository < CalibrationEquipment, Long > {
    
}
