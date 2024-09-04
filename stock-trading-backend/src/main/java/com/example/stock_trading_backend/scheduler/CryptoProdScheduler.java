package com.example.stock_trading_backend.scheduler;

import com.example.stock_trading_backend.services.implementations.CryptoServiceImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class CryptoProdScheduler {

    private final CryptoServiceImpl cryptoService;

    public CryptoProdScheduler(CryptoServiceImpl cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Scheduled(fixedRateString = "900000") // half hour
    public void runJob() {
        cryptoService.getCryptosFromApiAndSaveToDb();
    }
}