package com.example.walletApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginResultRepository extends JpaRepository<LoginResult, Long> {
    @Query("SELECT u FROM LoginResult u WHERE u.user = ?1 AND u.success = ?2 AND u.date = (SELECT MAX(lr.date) FROM LoginResult lr WHERE lr.user = u.user AND lr.success = u.success)")    public LoginResult findLoginResultByUser(User user, boolean succ);


}