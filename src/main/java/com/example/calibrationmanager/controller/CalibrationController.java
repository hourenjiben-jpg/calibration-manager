package com.example.calibrationmanager.controller;

import com.example.calibrationmanager.model.CalibrationEquipment;
import com.example.calibrationmanager.model.CalibrationLog;
import com.example.calibrationmanager.model.Tenant;
import com.example.calibrationmanager.model.User;
import com.example.calibrationmanager.repository.CalibrationRepository;
import com.example.calibrationmanager.repository.CalibrationLogRepository;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
    public String title(Model model, @AuthenticationPrincipal User currentUser) {

        Tenant tenant = currentUser.getTenant();
        List<CalibrationEquipment> equipment = repository.findByTenant(tenant);

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
    public String registerPost(CalibrationEquipment equipment, @AuthenticationPrincipal User currentUser) {
        // 登録機器に操作中ユーザーのテナントを強制的に紐付け
        equipment.setTenant(currentUser.getTenant());

        LocalDate date = equipment.getCalibrationDate();
        int period = equipment.getPeriod();

        LocalDate targetDate = date.plusMonths(period).with(TemporalAdjusters.lastDayOfMonth());

        equipment.setCalibrationDeadline(targetDate);

        repository.save(equipment);

        return "redirect:/title";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable("id") Long id, @AuthenticationPrincipal User currentUser) {

        CalibrationEquipment equipment = repository.findById(id).orElse(null);
        if(equipment != null && equipment.getTenant().getId().equals(currentUser.getTenant().getId())) {
            repository.deleteById(id);
        }

        return "redirect:/title";
    }

    @GetMapping("/edit/{id}")
    public String editEuqipment(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User currentUser) {

        CalibrationEquipment equipment =  repository.findById(id).orElse(null);

        if(equipment == null || !equipment .getTenant().getId().equals(currentUser.getTenant().getId())) {
            return "redirect:/title";
        }
        model.addAttribute("equipment", equipment);

        return "edit";
    }

    @PostMapping("/edit")
    public String editPost(CalibrationEquipment equipment, @AuthenticationPrincipal User currentUser) {

        equipment.setTenant(currentUser.getTenant());
        repository.save(equipment);

        return "redirect:/title";
    }

    @GetMapping("/calibrate/{id}")
    public String calibrateGet(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User currentUser) {
        CalibrationEquipment equipment = repository.findById(id)
                  .orElseThrow(() -> new IllegalArgumentException("無効な機器ID:" + id));

        if(!equipment.getTenant() .getId().equals(currentUser.getTenant().getId())) {
            return "redirect:/title";
        }          
        model.addAttribute("equipment", equipment);

        return "calibrate";
    }

    @PostMapping("/calibrate")
    public String calibratePost(
        @RequestParam("equipmentId") Long equipmentId,
        @RequestParam("calibrationDate") String calibrationDate,
        @RequestParam("result") String result,
        @RequestParam("note") String note,
        @AuthenticationPrincipal User currentUser
     ) {

        CalibrationEquipment equipment = repository.findById(equipmentId)
                  .orElseThrow(() -> new IllegalArgumentException("無効な機器ID:" + equipmentId));

        if(!equipment.getTenant().getId().equals(currentUser.getTenant().getId())) {
            return "redirect:/title";
        }
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
