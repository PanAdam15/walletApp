package com.example.walletApp.Controller;

import com.example.walletApp.AESenc;
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

import java.security.Key;
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
    public String addNewPass(String walletPassword, String login) throws Exception {
        Password p = new Password();
        p.setLogin(login);
        p.setUser(getUser());
        p.setSecretKey(getUser().getSecretKey());
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
    public String updatePassword(@PathVariable("id") long id, Password password, @RequestParam(name = "sharedTo") String sharedTo,
                                 BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            password.setId(id);
            return "edit_pass";
        }
        password.setWalletPassword(aeSenc.encrypt(password.getWalletPassword(), getUser().getSecretKey()));
        password.setUser(getUser());
        password.setSecretKey(getUser().getSecretKey());
        password.setSharedFrom(getUser().getLogin());
        password.setSharedTo(sharedTo);
        passwordRepo.save(password);
        return "add_success_page";
    }

    //TODO: poprawic szyfrowanie w udostepnianym hasle bo gdzies 2 razy szyfrowanie jest
    @GetMapping("/share/{id}")
    public String sharePassword(@PathVariable("id") long id, Model model) throws Exception {
        Password password = passwordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass Id:" + id));
        model.addAttribute("user", getUser());
        model.addAttribute("password", password);
        model.addAttribute("hash", sha256String);
        return "share_password";
    }

    @RequestMapping(value = "/shared/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleSharing(@PathVariable("id") long id, @RequestParam(name = "sharedTo") String sharedTo, Password password,
                                BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            password.setId(id);
            return "share_password";
        }
        SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        //|| Objects.equals("{pbkdf2}" + encoder.encode(userPassword), user.getPassword())
        // User user = userRepo.findByLogin(getUser().getLogin());
        password.setSecretKey(getUser().getSecretKey());
        password.setSharedTo(sharedTo);
        password.setSharedFrom(getUser().getLogin());
        password.setUser(getUser());
        passwordRepo.save(password);
        return "share_success";
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
        //Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        //|| Objects.equals("{pbkdf2}" + encoder.encode(userPassword), user.getPassword())

        try {
            User sharedFrom = userRepo.findByLogin(password.getSharedFrom());
            User sharedTo = userRepo.findByLogin(password.getSharedTo());


        if (Objects.equals(userPassword, getUser().getSecondPassword())) {
           // if(sharedFrom==getUser()) {
                String decrypted = aeSenc.decrypt(password.getWalletPassword(), getUser().getSecretKey());
                model.addAttribute("decrypted_password", decrypted);
          //  }else if(sharedTo==getUser()) {
//                String decrypted = aeSenc.decrypt(password.getWalletPassword(), sharedFrom.getSecretKey());
//                model.addAttribute("decrypted_password", decrypted);
          //  }
           return "encrypted_pass";
        } else
            return "error_value";
        } catch (NullPointerException e) {}

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
