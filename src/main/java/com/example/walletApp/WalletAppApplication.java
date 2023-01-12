package com.example.walletApp;

import com.example.walletApp.Controller.AppController;
import com.example.walletApp.Entity.User;
import com.example.walletApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication()
public class WalletAppApplication {
	@Autowired
	private UserRepository dao;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(WalletAppApplication.class, args);
	}

	AppController appController = new AppController();


//	@PostConstruct
//	public void init() {
//		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
//		dao.save(new User("admin",
//				passwordEncoder.encode("admin"),"admin"));
//		dao.save(new User("ania",
//				passwordEncoder.encode("ania"),"ania"));
//	}
}
