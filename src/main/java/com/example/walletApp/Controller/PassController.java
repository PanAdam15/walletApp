package com.example.walletApp.Controller;

import com.example.walletApp.*;
import com.example.walletApp.Entity.Password;
import com.example.walletApp.Entity.User;
import com.example.walletApp.Repository.PasswordRepository;
import com.example.walletApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


@Controller
public class PassController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordRepository passwordRepo;
    private AESenc aeSenc = new AESenc();
    private String sha256String;

    @PostMapping("/process_new_pass")
    public String addNewPass(String description, String login) throws Exception {
        Password p = new Password();
        p.setLogin(login);
        p.setUser(getUser());
        p.setDescription(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        String formatedDate = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).format(formatter);
        p.setDate(formatedDate);
        p.setStatus("Created");
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
        password.setDescription(password.getDescription());
        password.setUser(getUser());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        String formatedDate = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).format(formatter);
        password.setDate(formatedDate);
        password.setStatus("Edited");
        passwordRepo.save(password);
        return "add_success_page";
    }

    @GetMapping("/decrypt/{id}")
    public String showDecryptForm(@PathVariable("id") long id, Model model) {
        Password password = passwordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass Id:" + id));
        model.addAttribute("user", getUser());
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
        SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        //|| Objects.equals("{pbkdf2}" + encoder.encode(userPassword), user.getPassword())
        if (Objects.equals(userPassword, getUser().getSecondPassword())) {
            String decrypted = aeSenc.decrypt(password.getWalletPassword(), getUser().getSecretKey());
            model.addAttribute("decrypted_password", decrypted);
            return "encrypted_pass";
        } else
            return "error_value";


    }
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByLogin(authentication.getName());
    }
}
