package com.example.walletApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
@Component
public class CustomAuthenticationFailureHandler
  implements AuthenticationFailureHandler {
 
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Override
    public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
      throws IOException, ServletException {

        //customUserDetailsService.recordLoginFailure(authentication);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, Object> data = new HashMap<>();
        data.put(
          "timestamp", 
          Calendar.getInstance().getTime());
        data.put(
          "exception", 
          exception.getMessage());

        response.getOutputStream()
          .println(objectMapper.writeValueAsString(data));
    }
}