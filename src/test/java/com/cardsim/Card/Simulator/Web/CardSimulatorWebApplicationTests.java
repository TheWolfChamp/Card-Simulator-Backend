package com.cardsim.Card.Simulator.Web;

import static org.assertj.core.api.Assertions.assertThat;

import com.cardsim.Card.Simulator.Web.Controllers.HomeController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = {"com.cardsim.Card.Simulator.Web.controller", "com.cardsim.Card.Simulator.Web.Service"})
class CardSimulatorWebApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public HomeController homeController() {
			return new HomeController();
		}
	}

	@Autowired
	private HomeController controller;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
}
