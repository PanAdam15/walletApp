package com.example.walletApp;

import com.example.walletApp.Controller.AppController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class WalletAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletAppApplication.class, args);
	}
	AppController appController = new AppController();

}
