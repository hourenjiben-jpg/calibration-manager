package com.example.calibrationmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Collection;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "username"})
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // 権限を返すメソッド
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

    // パスワードをシステムに教えるメソッド
    @Override
    public String getPassword() {return this.password; }

    // ユーザー名をシステムに教えるメソッド
    @Override
    public String getUsername() {return this.username; }

    // アカウントの期限チェック
    @Override
    public boolean isAccountNonExpired() {return true; }

    // アカウントのロックチェック
    @Override
    public boolean isAccountNonLocked() {return true; }

    // パスワードの期限チェック
    @Override
    public boolean isCredentialsNonExpired() {return true; }

    // アカウントの有効性チェック
    @Override
    public boolean isEnabled() {return true; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
}
