package com.cardsim.Card.Simulator.Web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CardSimulatorWebApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
        .directory("")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();
		SpringApplication.run(CardSimulatorWebApplication.class, args);
	}

}
