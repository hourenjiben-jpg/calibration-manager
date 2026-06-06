package com.example.calibrationmanager.repository;

import com.example.calibrationmanager.model.CalibrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalibrationLogRepository extends JpaRepository<CalibrationLog, Long>{
    
}
