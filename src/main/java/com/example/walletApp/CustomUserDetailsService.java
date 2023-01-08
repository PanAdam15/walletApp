package com.example.walletApp;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
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
                    true, true, true, true, grupa);
        } else {
            throw
                    new UsernameNotFoundException("Zły login lub hasło.");
        }
    }
 
}