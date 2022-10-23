package com.example.walletApp;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class AppController {
 
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordRepository passwordRepo;
     
    @GetMapping("")
    public String viewHomePage () {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @GetMapping("/new")
    public String showNewPassForm(Model model) {
        model.addAttribute("password", new Password());

        return "add_new_pass";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepo.save(user);

        return "register_success";
    }

    @PostMapping("/process_new_pass")
    public @ResponseBody String addNewPass (String password, String login){
        Password p = new Password();
        p.setLogin(login);
        p.setPassword(password);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginName = authentication.getName();
        User user = userRepo.findByLogin(userLoginName);
        p.setUser(user);
        passwordRepo.save(p);

        return "users";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        List<Password> listPasswords = passwordRepo.findAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("listPasswords",listPasswords);
        return "users";
    }
}