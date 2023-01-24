package com.example.walletApp.Repository;
 
import com.example.walletApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.login = ?1")
    public User findByLogin(String login);
}