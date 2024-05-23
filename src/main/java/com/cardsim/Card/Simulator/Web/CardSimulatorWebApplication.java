package com.cardsim.Card.Simulator.Web;

import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.io.IOException;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class CardSimulatorWebApplication {

	public static void main(String[] args) throws IOException, FirebaseAuthException {
		SpringApplication.run(CardSimulatorWebApplication.class, args);
	}

}
