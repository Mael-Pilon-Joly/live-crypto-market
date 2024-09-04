package com.example.stock_trading_backend.controllers;
import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.services.interfaces.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cryptos")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @PostMapping
    public ResponseEntity<Crypto> createCrypto(@RequestBody Crypto crypto) {
        Crypto createdCrypto = cryptoService.createCrypto(crypto);
        return new ResponseEntity<>(createdCrypto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crypto> getCryptoById(@PathVariable Long id) throws EntityNotFoundException {
        Crypto crypto = cryptoService.getCryptoById(id);
        return new ResponseEntity<>(crypto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crypto> updateCrypto(@PathVariable Long id, @RequestBody Crypto crypto) throws EntityNotFoundException {
        Crypto updatedCrypto = cryptoService.updateCrypto(id, crypto);
        return new ResponseEntity<>(updatedCrypto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrypto(@PathVariable Long id) throws EntityNotFoundException {
        cryptoService.deleteCrypto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/asset-id")
    public ResponseEntity<Crypto> getCryptoByAssetId(@RequestParam String assetId) throws EntityNotFoundException {
        Crypto crypto = cryptoService.getCryptoByAssetId(assetId);
        return new ResponseEntity<>(crypto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Crypto>> getAllStocks() {
        List<Crypto> cryptos = cryptoService.getAllCryptos();
        return new ResponseEntity<>(cryptos, HttpStatus.OK);
    }
}