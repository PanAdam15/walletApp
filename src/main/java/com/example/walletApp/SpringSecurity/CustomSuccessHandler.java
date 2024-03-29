package com.example.walletApp.SpringSecurity;


import com.example.walletApp.Service.CustomUserDetailsService;
import com.example.walletApp.Entity.LoginResult;
import com.example.walletApp.Entity.User;
import com.example.walletApp.Repository.LoginResultRepository;
import com.example.walletApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginResultRepository loginRepository;

    @Autowired
    private CustomUserDetailsService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String userName = authentication.getName();
        User user = userRepository.findByLogin(userName);
        WebAuthenticationDetails details = (WebAuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        String ipAddress = request.getRemoteAddr();
        LoginResult loginResult= new LoginResult();
        loginResult.setUser(user);
        loginResult.setIp(ipAddress);
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = date.format(formatter);
        loginResult.setDate(formatDateTime);
        loginResult.setSuccess(true);
        loginRepository.save(loginResult);
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user.getLogin());
        }
        redirectStrategy.sendRedirect(request, response, "/users");
    }
}