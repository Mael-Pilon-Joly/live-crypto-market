package com.example.stock_trading_backend.scheduler;

import com.example.stock_trading_backend.services.implementations.PortfolioValueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PortfolioValueScheduler {

    @Autowired
    private PortfolioValueServiceImpl portfolioValueService;

    @Scheduled(fixedRateString = "1800000") // Runs every hour at the top of the hour
    public void calculatePortfolioValuesEveryHour() {
        portfolioValueService.calculatePortfolioValues();
    }
}
