package com.example.calibrationmanager.controller;

import com.example.calibrationmanager.model.User;
import com.example.calibrationmanager.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Controller
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // メンバー追加画面表示
    @GetMapping("/admin/users/add")
    @Transactional(readOnly = true)
    public String addUsersForm(@AuthenticationPrincipal User loginUser, Model model) {
        // ログイン管理者のデータを引き直す
        User currentUser = userRepository.findById(loginUser.getId()).orElseThrow();

        List<User> companyUsers = userRepository.findByTenant_Id(currentUser.getTenant().getId());
        model.addAttribute("users", companyUsers);

        return "add-user";
    }

    // メンバー追加保存
    @PostMapping("/admin/users/add")
    @Transactional
    public String addUser(
        @RequestParam("username") String newUsername,
        @RequestParam("password") String newPassword,
        @AuthenticationPrincipal User loginUser,
        Model model) {

            User currentUser = userRepository.findById(loginUser.getId())
                 .orElseThrow(() -> new IllegalStateException("ログインユーザーが見つかりません。"));

            //　ログイン管理者の企業情報取得
            var tenant = currentUser.getTenant();

            // 同じ企業の中に既に同じユーザー名がいないかチェック
            boolean userExists = userRepository.findByTenant_TenantCodeAndUsername(tenant.getTenantCode(), newUsername).isPresent();
            if (userExists) {
                model.addAttribute("errorMessage", "このユーザーは、社内で既に使用されています。");

                model.addAttribute("users", userRepository.findByTenant_Id(tenant.getId()));
                return "add-user";
            }

            // 新しいユーザーを作成し、管理者と同じ企業をセット
            User newUser = new User();
            newUser.setUsername(newUsername);
            newUser.setPassword(passwordEncoder.encode(newPassword)); // パスワード暗号化
            newUser.setTenant(tenant); // 企業紐付け

            userRepository.save(newUser);

            model.addAttribute("successMessage", "新メンバー「" + newUsername + "」さんを登録しました。");

            model.addAttribute("users", userRepository.findByTenant_Id(tenant.getId()));
            return "add-user";
        }

        @PostMapping("/admin/users/reset-password")
        @Transactional
        public String resetPassword(
            @RequestParam("userId") Long targetUserId,
            @RequestParam("newPassword") String newPassword,
            @AuthenticationPrincipal User loginUser,
            Model model) {

                User currentUser = userRepository.findById(loginUser.getId()).orElseThrow();
                User targetUser = userRepository.findById(targetUserId)
                      .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません。"));
             
            if (!targetUser.getTenant().getId().equals(currentUser.getTenant().getId())){
                model.addAttribute("errorMessage", "不正な操作です。他社のパスワードは変更できません。");
                model.addAttribute("users", userRepository.findByTenant_Id(currentUser.getTenant().getId()));
                return "add-user";           
             }        
             
             //　パスワード上書きして暗号化保存
             targetUser.setPassword(passwordEncoder.encode(newPassword));
             userRepository.save(targetUser);

             model.addAttribute("successMessage", "「" + targetUser.getUsername() + "」さんのパスワードを更新しました。");

             model.addAttribute("users", userRepository.findByTenant_Id(currentUser.getTenant().getId()));
             return "add-user";

            }
    
    
}
