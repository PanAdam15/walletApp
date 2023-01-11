package com.example.walletApp;

import com.example.walletApp.Entity.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Key;

import static org.testng.Assert.*;

public class UserTest {
User user;
String PASSWORD = "12345";
String LOGIN = "Staszek";
Key SECRET_KEY = AESenc.generateKey();
    @BeforeMethod
    public void setUp() {
        user = new User();
    }

    @Test
    public void test(){
        user.setPassword(PASSWORD);
        user.setLogin(LOGIN);
        user.setSecretKey(SECRET_KEY);
        assertEquals(PASSWORD,user.getPassword());
        assertEquals(LOGIN,user.getLogin());
        assertEquals(SECRET_KEY,user.getSecretKey());
    }
}