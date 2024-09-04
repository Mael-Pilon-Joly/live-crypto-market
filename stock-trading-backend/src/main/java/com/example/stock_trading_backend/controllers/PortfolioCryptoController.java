package com.example.stock_trading_backend.controllers;

import com.example.stock_trading_backend.entities.PortfolioCrypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.services.interfaces.PortfolioCryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio-cryptos")
public class PortfolioCryptoController {

    @Autowired
    private PortfolioCryptoService portfolioCryptoService;

    @PostMapping
    public ResponseEntity<PortfolioCrypto> createPortfolioCrypto(@RequestBody PortfolioCrypto portfolioCrypto) {
        PortfolioCrypto createdPortfolioCrypto = portfolioCryptoService.createPortfolioCrypto(portfolioCrypto);
        return new ResponseEntity<>(createdPortfolioCrypto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioCrypto> getPortfolioCryptoById(@PathVariable Long id) throws EntityNotFoundException {
        PortfolioCrypto portfolioCrypto = portfolioCryptoService.getPortfolioCryptoById(id);
        return new ResponseEntity<>(portfolioCrypto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolioCrypto(@PathVariable Long id) throws EntityNotFoundException {
        portfolioCryptoService.deletePortfolioCrypto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<PortfolioCrypto>> getPortfolioCryptoByPortfolioId(@PathVariable Long portfolioId) {
        List<PortfolioCrypto> portfolioCryptos = portfolioCryptoService.getPortfolioCryptosByPortfolioId(portfolioId);
        return new ResponseEntity<>(portfolioCryptos, HttpStatus.OK);
    }
}