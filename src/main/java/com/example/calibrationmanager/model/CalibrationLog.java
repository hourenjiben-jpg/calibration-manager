package com.example.calibrationmanager.model;

import com.example.calibrationmanager.model.CalibrationEquipment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDate;

@Entity
public class CalibrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID
    private LocalDate date; // 校正日
    private String result; // 結果
    private String memo; // メモ

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private CalibrationEquipment equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public CalibrationEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(CalibrationEquipment equipment) {
        this.equipment = equipment;
    }

}
