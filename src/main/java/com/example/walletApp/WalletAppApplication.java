package com.example.walletApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

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


	@PostConstruct
	public void init() {
		dao.save(new User("admin",
				passwordEncoder.encode("admin")));
		dao.save(new User("ania",
				passwordEncoder.encode("ania")));
	}
}
