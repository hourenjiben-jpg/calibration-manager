package com.example.calibrationmanager.controller;

import com.example.calibrationmanager.model.CalibrationEquipment;
import com.example.calibrationmanager.model.CalibrationLog;
import com.example.calibrationmanager.repository.CalibrationRepository;
import com.example.calibrationmanager.repository.CalibrationLogRepository;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;

@Controller
public class CalibrationController {

    @Autowired
    private CalibrationRepository repository;  // repositoryをnewしなくても使用できるようになる
    @Autowired
    private CalibrationLogRepository logRepository;

    @GetMapping("/title")
    public String title(Model model) {
        List<CalibrationEquipment> equipment = repository.findAll();

        model.addAttribute("item", equipment);

        return "index"; // index.htmlで表示する
    }

    @GetMapping("/register")
    public String register(Model model) {

        CalibrationEquipment equipment = new CalibrationEquipment();
        model.addAttribute("register", equipment);

        return "register";
    }

    @PostMapping("/register")
    public String registerPost(CalibrationEquipment equipment) {
        LocalDate date = equipment.getCalibrationDate();
        int period = equipment.getPeriod();

        LocalDate targetDate = date.plusMonths(period).with(TemporalAdjusters.lastDayOfMonth());

        equipment.setCalibrationDeadline(targetDate);

        repository.save(equipment);

        return "redirect:/title";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable("id") Long id) {
        repository.deleteById(id);

        return "redirect:/title";
    }

    @GetMapping("/edit/{id}")
    public String editEuqipment(@PathVariable("id") Long id, Model model) {

        CalibrationEquipment equipment =  repository.findById(id).orElse(null);

        model.addAttribute("equipment", equipment);

        return "edit";
    }

    @PostMapping("/edit")
    public String editPost(CalibrationEquipment equipment) {

        repository.save(equipment);

        return "redirect:/title";
    }

    @GetMapping("/calibrate/{id}")
    public String calibrateGet(@PathVariable("id") Long id, Model model) {
        CalibrationEquipment equipment = repository.findById(id)
                  .orElseThrow(() -> new IllegalArgumentException("無効な機器ID:" + id));

        model.addAttribute("equipment", equipment);

        return "calibrate";
    }

    @PostMapping("/calibrate")
    public String calibratePost(
        @RequestParam("equipmentId") Long equipmentId,
        @RequestParam("calibrationDate") String calibrationDate,
        @RequestParam("result") String result,
        @RequestParam("note") String note
     ) {

        CalibrationEquipment equipment = repository.findById(equipmentId)
                  .orElseThrow(() -> new IllegalArgumentException("無効な機器ID:" + equipmentId));

        CalibrationLog log = new CalibrationLog();

        log.setEquipment(equipment);
        log.setDate(LocalDate.parse(calibrationDate));
        log.setResult(result);
        log.setMemo(note);

        logRepository.save(log);

        if (result.equals("合格")) {
            equipment.setStatus("有効");
            LocalDate nextDeadline = LocalDate.parse(calibrationDate).plusMonths(equipment.getPeriod()).with(TemporalAdjusters.lastDayOfMonth());
            equipment.setCalibrationDeadline(nextDeadline);
        } else {
            equipment.setStatus("使用停止");
            equipment.setCalibrationDeadline(null);
        }

        repository.save(equipment);

        return "redirect:/title";

    }
    
}
