package com.example.walletApp.Service;

import com.example.walletApp.Entity.User;
import com.example.walletApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class UserServices {
 
    public static final int MAX_FAILED_ATTEMPTS = 3;
     
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours
     
    @Autowired
    private UserRepository repo;
     
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        repo.updateFailedAttempts(newFailAttempts, user.getLogin());
    }
     
    public void resetFailedAttempts(String email) {
        repo.updateFailedAttempts(0, email);
    }
     
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
         
        repo.save(user);
    }
     
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
             
            repo.save(user);
             
            return true;
        }
         
        return false;
    }
}