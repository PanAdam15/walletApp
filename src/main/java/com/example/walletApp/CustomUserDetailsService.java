package com.example.walletApp;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
 
    @Autowired
    private UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByLogin(username);
        if (user != null) {
            List<GrantedAuthority> grupa = new ArrayList<>();
            grupa.add(new SimpleGrantedAuthority("normalUser"));
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(), user.getPassword(),
                    user.isEnabled(), true, true, user.isAccountNonLocked(), grupa);
        } else {
            throw
                    new UsernameNotFoundException("Zły login lub hasło.");
        }
    }

    public static final int TWO_FAILED = 2;
    public static final int THREE_FAILED = 3;
    public static final int FOUR_FAILED = 4;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    public static final long FIVE_SECONDS_LOCK = 5 * 1000; // 24 hours
    public static final long TEN_SECONDS_LOCK =  10 * 1000; // 24 hours
    public static final long TWO_MINUTES_LOCK = 2 * 60 * 1000; // 24 hours



    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepo.updateFailedAttempts(newFailAttempts, user.getLogin());
    }

    public void lockedAccountFor(User user, Long lockedFor){
        userRepo.updateLockedFor(lockedFor, user.getLogin());
    }

    public void resetFailedAttempts(String login) {
        userRepo.updateFailedAttempts(0, login);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        userRepo.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + user.getLockedFor() < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            //user.setFailedAttempt(0);

            userRepo.save(user);

            return true;
        }

        return false;
    }
}