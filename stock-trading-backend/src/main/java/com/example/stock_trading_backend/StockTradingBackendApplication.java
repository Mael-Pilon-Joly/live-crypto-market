package com.example.stock_trading_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockTradingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTradingBackendApplication.class, args);
	}

}
