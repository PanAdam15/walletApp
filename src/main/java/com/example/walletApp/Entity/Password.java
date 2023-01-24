package com.example.walletApp.Entity;
 
import com.example.walletApp.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "passwords")
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String walletPassword;

    @Column
    private String status;

    @Column
    private String description;

    @Column
    private String login;

    @Column
    private String date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonIgnoreProperties({"passwords"})
    private User user;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String web_address) {
        this.status = web_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}