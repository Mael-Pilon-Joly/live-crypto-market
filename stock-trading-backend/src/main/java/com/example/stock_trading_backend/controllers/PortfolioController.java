package com.example.stock_trading_backend.controllers;


import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.PortfolioRepository;
import com.example.stock_trading_backend.services.interfaces.PortfolioService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
        return new ResponseEntity<>(createdPortfolio, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) throws EntityNotFoundException {
        Portfolio portfolio = portfolioService.getPortfolioById(id);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long id, @RequestBody Portfolio portfolio) throws EntityNotFoundException {
        Portfolio updatedPortfolio = portfolioService.updatePortfolio(id, portfolio);
        return new ResponseEntity<>(updatedPortfolio, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) throws EntityNotFoundException {
        portfolioService.deletePortfolio(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/worth/{id}")
    public ResponseEntity<Double> getWorthByPortfolioId(@PathVariable Long id) throws EntityNotFoundException {
        double totalPortfolioWorth = portfolioService.getCurrentPortfolioWorth(id);
        return new ResponseEntity<>(totalPortfolioWorth, HttpStatus.OK);
    }

    @GetMapping("/portfolios")
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        return new ResponseEntity<>(portfolioRepository.findAll(), HttpStatus.OK);
    }
}
