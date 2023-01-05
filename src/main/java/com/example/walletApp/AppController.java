package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
public class AppController {

    @Value("${user.pepper}")
    private String pepper;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordRepository passwordRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String sha256String;

    private AESenc aeSenc = new AESenc();

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        //zwrócenie nazwy widoku logowania - login.html
        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        List<Password> listPasswords = passwordRepo.findByUser(getUser());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("listPasswords", listPasswords);
        return "users";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, Boolean hash) throws Exception {
        user.setSecondPassword(user.getPassword());
        if (hash) {
            SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
            user.setPassword("{scrypt}" + passwordEncoder.encode(user.getPassword()));
        } else {
            Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
            user.setPassword("{pbkdf2}" + encoder.encode(user.getPassword()));
        }
        if(user.getSecretKey()==null)
        user.setSecretKey(AESenc.generateKey());
        userRepo.save(user);

        return "register_success";
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByLogin(authentication.getName());
    }


    @GetMapping("/change_password")
    public String showChangePasswordForm(User user, Model model) {
        user = userRepo.findByLogin(getUser().getLogin());
        model.addAttribute("user", user);
        return "change_password";
    }

    @RequestMapping("/update_password")
    public String changePassword(Boolean hash, @RequestParam(name = "oldPassword") String oldPassword, @RequestParam(name = "newPassword") String newPassword, User user,
                                 BindingResult result, Model model) throws Exception {
        user = getUser();

        if (Objects.equals(oldPassword, user.getSecondPassword())) {
            if (hash) {
                SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
                user.setPassword("{scrypt}" + passwordEncoder.encode(newPassword));
            } else {
                Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
                user.setPassword("{pbkdf2}" + encoder.encode(newPassword));
            }
            user.setSecondPassword(newPassword);
            userRepo.save(user);
            return "add_success_page";
        }
        return "error_value";
    }
}