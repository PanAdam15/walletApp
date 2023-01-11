//package com.example.walletApp;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.logging.Logger;
//
//@Component
//public class MyApplicationListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
//
//    @Autowired
//    private CustomUserDetailsService customUserDetailsService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private LoginResultRepository loginRepository;
//
//    @Override
//    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
//        AuthenticationException exception;
//        String userName = event.getAuthentication().getName();
//        User user = userRepository.findByLogin(userName);
//        if (user != null) {
//            WebAuthenticationDetails details = (WebAuthenticationDetails) event.getAuthentication().getDetails();
//            String ipAddress = details.getRemoteAddress();
//            LoginResult loginResult = new LoginResult();
//            loginResult.setIp(ipAddress);
//            loginResult.setUser(user);
//            LocalDateTime date = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String formatDateTime = date.format(formatter);
//            loginResult.setDate(formatDateTime);
//            loginResult.setSuccess(false);
//
//            loginRepository.save(loginResult);
//            if (user.isEnabled() && user.isAccountNonLocked()) {
//                if (user.getFailedAttempt() < UserServices.MAX_FAILED_ATTEMPTS - 1) {
//                    customUserDetailsService.increaseFailedAttempts(user);
//                } else {
//                    customUserDetailsService.lock(user);
//                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
//                            + " It will be unlocked after 24 hours.");
//                }
//            } else if (!user.isAccountNonLocked()) {
//                if (customUserDetailsService.unlockWhenTimeExpired(user)) {
//                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
//                }
//            }
//        }
//    }
//}