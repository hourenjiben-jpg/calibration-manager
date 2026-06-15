package com.example.calibrationmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CalibrationEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID
    private String name; // 機器名
    private String modelName; // 型式
    private String serialNumber; // 製造番号
    private LocalDate calibrationDate;
    private LocalDate calibrationDeadline;
    private int period; // 周期
    private String status = "有効"; // 有効、無効

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalibrationLog> calibrationLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getCalibrationDate() {
        return calibrationDate;
    }

    public void setCalibrationDate(LocalDate calibrationDate) {
        this.calibrationDate = calibrationDate;
    }

    public LocalDate getCalibrationDeadline() {
        return calibrationDeadline;
    }

    public void  setCalibrationDeadline(LocalDate calibrationDeadline) {
        this.calibrationDeadline = calibrationDeadline;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CalibrationLog> getCalibrationLogs() {
        return calibrationLogs;
    }

    public void setCalibrationLogs(List<CalibrationLog> calibrationLog) {
        this.calibrationLogs = calibrationLog;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}

