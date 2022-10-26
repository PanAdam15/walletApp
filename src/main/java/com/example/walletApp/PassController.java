package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PassController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordRepository passwordRepo;


}
