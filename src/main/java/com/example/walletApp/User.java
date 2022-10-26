package com.example.walletApp;
 
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.security.Key;
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