package com.example.walletApp;
 
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     
    @Column(nullable = false, unique = true, length = 45)
    private String login;
     
    @Column(nullable = false, length = 512)
    private String password;

    @Column
    private Key secretKey;

    @Column
    private String secondPassword;

    @Column
    private Integer failureCount;

    @Column
    private String ip;

    @Column
    private LocalDateTime date;

    @Column
    private boolean loginResult;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isLoginResult() {
        return loginResult;
    }

    public void setLoginResult(boolean loginResult) {
        this.loginResult = loginResult;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public User() {
    }
    public User(String login,
                String password) {

        this.login = login;
        this.password = password;
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public Key getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}