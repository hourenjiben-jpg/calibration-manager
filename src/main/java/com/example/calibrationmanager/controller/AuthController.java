package com.example.calibrationmanager.controller;

import com.example.calibrationmanager.model.Tenant;
import com.example.calibrationmanager.model.User;
import com.example.calibrationmanager.repository.UserRepository;
import com.example.calibrationmanager.service.TenantService;
import com.example.calibrationmanager.repository.TenantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    // コンストラクタで道具を自動準備
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TenantService tenantService, TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantService = tenantService;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupUser(@ModelAttribute User user,
                             @RequestParam("tenantCode") String tenantCode,
                             @RequestParam("tenantName") String tenantName,
                              Model model) {

        // 企業番号の重複チェック
        boolean tenantExists = tenantRepository.existsByTenantCode(tenantCode);
        if (tenantExists) {
            model.addAttribute("errorMessage", "この企業ログイン番号はすでに使用されています。別の番号を入力して下さい。");
            return "signup";
        }

        // 同じ企業号の中に同じユーザーがいないかチェック
        boolean userExists = userRepository.findByTenant_TenantCodeAndUsername(tenantCode, user.getUsername()).isPresent();
        if (userExists) {
            model.addAttribute("errorMessage", "この企業番号の中に、同じユーザー名がすでに登録されています。");
            return "signup";
        }

        Tenant newTenant = tenantService.createAndSaveTenant(tenantCode, tenantName);
        user.setTenant(newTenant);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return "redirect:/login";
    }
    
}
