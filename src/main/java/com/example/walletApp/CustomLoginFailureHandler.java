package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private CustomUserDetailsService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginResultRepository loginResultRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String login = request.getParameter("login");
        User user = userRepository.findByLogin(login);
        if (user != null) {
            String ipAddress = request.getRemoteAddr();
            LoginResult loginResult = new LoginResult();
            loginResult.setIp(ipAddress);
            loginResult.setUser(user);
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = date.format(formatter);
            loginResult.setDate(formatDateTime);
            loginResult.setSuccess(false);

            loginResultRepository.save(loginResult);
            userService.increaseFailedAttempts(user);
            System.out.println("KUTASOW W DUPIE SZUBIGI:" + user.getFailedAttempt());


            if(user.isEnabled() && user.isAccountNonLocked()){
            if (user.getFailedAttempt() < CustomUserDetailsService.TWO_FAILED - 1) {
                super.setDefaultFailureUrl("/login?error");
                System.out.println("KUTASOW W DUPIE SZUBIGI:" + user.getFailedAttempt());
            } else if (user.getFailedAttempt() == CustomUserDetailsService.TWO_FAILED - 1) {
                super.setDefaultFailureUrl("/login?error2");
                userService.lock(user);
                userService.lockedAccountFor(user,CustomUserDetailsService.FIVE_SECONDS_LOCK);
                System.out.println("KUTASOW W DUPIE SZUBIGI:" + user.getFailedAttempt());
//                    exception = new LockedException("Your account has been locked due to 2 failed attempts."
//                            + " It will be unlocked after 5 seconds.");
            } else if (user.getFailedAttempt() == CustomUserDetailsService.THREE_FAILED - 1) {
                super.setDefaultFailureUrl("/login?error3");
                userService.lock(user);
                userService.lockedAccountFor(user, CustomUserDetailsService.TEN_SECONDS_LOCK);
                System.out.println("KUTASOW W DUPIE SZUBIGI:" + user.getFailedAttempt());
//                    exception = new LockedException("Your account has been locked due to 2 failed attempts."
//                            + " It will be unlocked after 10 seconds.");
            } else if (user.getFailedAttempt() >= CustomUserDetailsService.FOUR_FAILED - 1) {
                super.setDefaultFailureUrl("/login?error4");
                userService.lock(user);
                userService.lockedAccountFor(user,CustomUserDetailsService.TWO_MINUTES_LOCK);
                System.out.println("KUTASOW W DUPIE SZUBIGI:" + user.getFailedAttempt());

            }
            }else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    super.setDefaultFailureUrl("/login?errorUn");
                }
            }

        }

        //super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);

    }
}
 