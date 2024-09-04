package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.entities.CryptoHistory;
import com.example.stock_trading_backend.repositories.CryptoHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CryptoHistoryServiceImpl {

    private final CryptoHistoryRepository cryptoHistoryRepository;

    @Autowired
    public CryptoHistoryServiceImpl(CryptoHistoryRepository cryptoHistoryRepository) {
        this.cryptoHistoryRepository = cryptoHistoryRepository;
    }

    public List<CryptoHistory> getCryptoHistory(String assetId) {
        return cryptoHistoryRepository.findByAssetIdOrderByTimestampDesc(assetId);
    }

    public List<CryptoHistory> getHistoryFromLastHour(LocalDateTime oneHourAgo) {
        return cryptoHistoryRepository.findCryptoHistoryFrom(oneHourAgo);
    }

    public List<CryptoHistory> getCryptoHistoryForLast4Hours(String assetId) {
        LocalDateTime fromTime = LocalDateTime.now().minusHours(4);
        System.out.println("Assets id =" + assetId + "fromTime =" + fromTime);
        return cryptoHistoryRepository.findCryptoHistoryForLast4Hours(assetId, fromTime);
    }
}