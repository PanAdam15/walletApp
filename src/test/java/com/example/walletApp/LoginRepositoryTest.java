package com.example.walletApp;

import com.mysql.cj.log.Log;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;
@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
public class LoginRepositoryTest {
@Autowired
LoginResultRepository loginRepository;
    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testFindLoginResultByUser() {
        User user = new User();



    }
}