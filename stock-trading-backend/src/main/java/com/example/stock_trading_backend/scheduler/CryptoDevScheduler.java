package com.example.stock_trading_backend.scheduler;


import com.example.stock_trading_backend.services.implementations.CryptoServiceImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class CryptoDevScheduler {

    private final CryptoServiceImpl cryptoService;

    public CryptoDevScheduler(CryptoServiceImpl cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Scheduled(fixedRateString = "1800000") // 1 hour
    public void runJob() {
        cryptoService.getCryptosFromApiAndSaveToDb();
    }
}
