package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.walletApp.AESenc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA512;

@Controller
public class AppController {

    @Value("${user.pepper}")
    private String pepper;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordRepository passwordRepo;

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
        List<Password> listPasswords = passwordRepo.findAll();
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
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            password.setId(id);
            return "edit_pass";
        }

        passwordRepo.save(password);
        return "add_success_page";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, Boolean hash) throws Exception {
        if(hash) {
            SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
            //String encodedBPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword("{scrypt}"+passwordEncoder.encode(user.getPassword()));
        }
        else {
            Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder ();
            //String encodedSPassword = encoder.encode(user.getPassword());
            user.setPassword("{pbkdf2}"+encoder.encode(user.getPassword()));
        }
        user.setSecretKey(AESenc.generateKey());
        userRepo.save(user);

        return "register_success";
    }

    @PostMapping("/process_new_pass")
    public String addNewPass(String walletPassword, String login) throws Exception {
        Password p = new Password();
        p.setLogin(login);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginName = authentication.getName();
        User user = userRepo.findByLogin(userLoginName);
        p.setUser(user);

        p.setWalletPassword(aeSenc.encrypt(walletPassword,user.getSecretKey()));

//        if(p.getHash())
//            p.setWalletPassword(calculateHMAC("salt"+walletPassword,"pepper"));
//        else
//            p.setWalletPassword(calculateSHA512("salt"+"pepper"+walletPassword));
// w hasłach - zrobić podstrone z detalami o hasle jak web adres opis i te ktore juz sa, potem klikajac przycisk odswiezac widok z zdekryptowanym haslem.

//        try {
//            String text = "My secret text.";
//            Key key = generateKey();
//            String encrypted = encrypt(text, key);
//            System.out.println("encrypted: " + encrypted);
//            String decrypted = decrypt(encrypted, key);
//            System.out.println("decrytped: " + decrypted);
//        } catch (Exception ex) {
//            Logger.getLogger(AESenc.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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





//
//    @PostMapping("/process_register")
//    public String processRegister(User user, Boolean hash) {
//        if(hash) {
//            AESenc aeSenc = new AESenc();
//            byte[] salt = aeSenc.getNextSalt();
//
//            String hmacEncodedPassword = aeSenc.calculateHMAC(user.getPassword(),salt);
//            user.setPassword(hmacEncodedPassword);
//            user.setSalt(salt);
//        }
//        else {
//            AESenc aeSenc = new AESenc();
//            byte[] salt = aeSenc.getNextSalt();
//            String shaEncodedPassword = aeSenc.calculateSHA512(pepper+salt+user.getPassword());
//            user.setPassword(shaEncodedPassword);
//            user.setSalt(salt);
//        }
//
//        userRepo.save(user);
//
//        return "register_success";
//    }
}