package com.example.UserApp.Repository;

import com.example.UserApp.Entity.Password;
import com.example.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    @Query("SELECT u FROM Password u WHERE u.user = ?1")
    public List<Password> findByUser(User user);
}