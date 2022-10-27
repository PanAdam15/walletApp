package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String sha256String;

    private AESenc aeSenc = new AESenc();

    @GetMapping("")
    public String viewHomePage() {
        return "index";
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

    @GetMapping("/new")
    public String showNewPassForm(Model model) {
        model.addAttribute("password", new Password());

        return "add_new_pass";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Password password = passwordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass Id:" + id));

        model.addAttribute("password", password);
        return "edit_pass";
    }

    @PostMapping("/update/{id}")
    public String updatePassword(@PathVariable("id") long id, Password password,
                                 BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            password.setId(id);
            return "edit_pass";
        }
        password.setWalletPassword(aeSenc.encrypt(password.getWalletPassword(), getUser().getSecretKey()));
        password.setUser(getUser());
        passwordRepo.save(password);
        return "add_success_page";
    }

    @GetMapping("/decrypt/{id}")
    public String showDecryptForm(@PathVariable("id") long id, Model model) {
        Password password = passwordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass Id:" + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginName = authentication.getName();
        User user = userRepo.findByLogin(userLoginName);
        model.addAttribute("user", user);
        model.addAttribute("password", password);
        model.addAttribute("hash", sha256String);
        return "decrypt_password";
    }

    @RequestMapping("/decrypted/{id}")
    public String handleDecryption(@PathVariable("id") long id, @RequestParam(name = "userPassword") String userPassword, Password password,
                                   BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            password.setId(id);
            return "decrypt_password";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginName = authentication.getName();
        User user = userRepo.findByLogin(userLoginName);
        SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        //|| Objects.equals("{pbkdf2}" + encoder.encode(userPassword), user.getPassword())
        if (Objects.equals(userPassword, user.getSecondPassword())) {
            String decrypted = aeSenc.decrypt(password.getWalletPassword(), user.getSecretKey());
            model.addAttribute("decrypted_password", decrypted);
            return "encrypted_pass";
        } else
            return "error_value";


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

    @PostMapping("/process_new_pass")
    public String addNewPass(String walletPassword, String login) throws Exception {
        Password p = new Password();
        p.setLogin(login);
        p.setUser(getUser());
        p.setWalletPassword(aeSenc.encrypt(walletPassword, getUser().getSecretKey()));
        passwordRepo.save(p);

        return "add_success_page";
    }

    @GetMapping("/delete/{id}")
    public String deletePass(@PathVariable("id") long id, Model model) {
        Password password = passwordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass Id:" + id));
        passwordRepo.delete(password);
        return "redirect:/users";
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginName = authentication.getName();
        User user = userRepo.findByLogin(userLoginName);
        return user;
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