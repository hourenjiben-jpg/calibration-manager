package com.example.calibrationmanager.service;

import com.example.calibrationmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String combinedUsername) throws UsernameNotFoundException {

    // データが届いてないまたは中身がからの場合
    if (combinedUsername == null || combinedUsername.trim().isEmpty()) {
        throw new UsernameNotFoundException("ログイン情報の形式が正しくありません。");
    }

    if (!combinedUsername.contains(",")) {
        throw new UsernameNotFoundException("企業番号またはユーザー名の入力形式が正しくありません。");
    }

    String[] parts = combinedUsername.split(",");

    if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
        throw new UsernameNotFoundException("企業番号とユーザー名の両方を入力して下さい。");
    }

    String tenantCode = parts[0];
    String username = parts[1];

    System.out.println("====== [デバッグ] ログイン成功ルートを通過中 ======");
        System.out.println("分解した企業番号: [" + tenantCode + "]");
        System.out.println("分解したユーザー名: [" + username + "]");
        System.out.println("==============================================");

    return userRepository.findByTenant_TenantCodeAndUsername(tenantCode, username)
            .orElseThrow(() -> new UsernameNotFoundException("企業番号またはユーザー名が違います。"));
}
    
}
