package com.example.walletApp;
 
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.login = ?1")
    public User findByLogin(String login);

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.login = ?2")
    @Modifying
    public void updateFailedAttempts(int failAttempts, String login);

    @Query("UPDATE User u SET u.lockedFor = ?1 WHERE u.login = ?2")
    @Modifying
    public void updateLockedFor(long lockedFor, String login);

}