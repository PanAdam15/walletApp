package com.example.walletApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

}