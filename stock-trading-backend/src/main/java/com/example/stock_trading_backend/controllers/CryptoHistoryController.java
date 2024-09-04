package com.example.stock_trading_backend.controllers;

import com.example.stock_trading_backend.entities.CryptoHistory;
import com.example.stock_trading_backend.services.implementations.CryptoHistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/api/crypto-history")
public class CryptoHistoryController {

    private final CryptoHistoryServiceImpl cryptoHistoryService;

    @Autowired
    public CryptoHistoryController(CryptoHistoryServiceImpl cryptoHistoryService) {
        this.cryptoHistoryService = cryptoHistoryService;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<List<CryptoHistory>> getCryptoHistory(@PathVariable String assetId) {
        List<CryptoHistory> history = cryptoHistoryService.getCryptoHistory(assetId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/last-hour")
    public ResponseEntity<List<CryptoHistory>> getLastHourCryptoHistory() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<CryptoHistory> history = cryptoHistoryService.getHistoryFromLastHour(oneHourAgo);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/last-4-hours")
    public List<CryptoHistory> getCryptoHistoryForLast4Hours(@RequestParam String assetId) {
        return cryptoHistoryService.getCryptoHistoryForLast4Hours(assetId);
    }
}
