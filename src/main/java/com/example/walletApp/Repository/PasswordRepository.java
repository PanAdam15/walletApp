package com.example.walletApp.Repository;

import com.example.walletApp.Entity.Password;
import com.example.walletApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    @Query("SELECT u FROM Password u WHERE u.user = ?1")
    public List<Password> findByUser(User user);

    @Query("SELECT u FROM Password u WHERE u.sharedTo = ?1 or u.sharedFrom = ?1")
    public List<Password> findBySharedTo(String sharedTo);

}