package com.example.stock_trading_backend.controllers;

import com.example.stock_trading_backend.entities.PortfolioValue;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.services.interfaces.PortfolioValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio-values")
public class PortfolioValueController {

    @Autowired
    private PortfolioValueService portfolioValueService;

    @PostMapping
    public ResponseEntity<PortfolioValue> createPortfolioValue(@RequestBody PortfolioValue portfolioValue) {
        PortfolioValue createdPortfolioValue = portfolioValueService.createPortfolioValue(portfolioValue);
        return new ResponseEntity<>(createdPortfolioValue, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioValue> getPortfolioValueById(@PathVariable Long id) throws EntityNotFoundException {
        PortfolioValue portfolioValue = portfolioValueService.getPortfolioValueById(id);
        return new ResponseEntity<>(portfolioValue, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioValue> updatePortfolioValue(@PathVariable Long id, @RequestBody PortfolioValue portfolioValue) throws EntityNotFoundException {
        PortfolioValue updatedPortfolioValue = portfolioValueService.updatePortfolioValue(id, portfolioValue);
        return new ResponseEntity<>(updatedPortfolioValue, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolioValue(@PathVariable Long id) throws EntityNotFoundException {
        portfolioValueService.deletePortfolioValue(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<PortfolioValue>> getPortfolioValuesByPortfolioId(@PathVariable Long portfolioId) {
        List<PortfolioValue> portfolioValues = portfolioValueService.getPortfolioValuesByPortfolioId(portfolioId);
        return new ResponseEntity<>(portfolioValues, HttpStatus.OK);
    }

    @GetMapping("/portfolio/{portfolioId}/range")
    public ResponseEntity<List<PortfolioValue>> getPortfolioValuesByDateRange(
            @PathVariable Long portfolioId,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate) {
        List<PortfolioValue> portfolioValues = portfolioValueService.getPortfolioValuesByDateRange(portfolioId, startDate, endDate);
        return new ResponseEntity<>(portfolioValues, HttpStatus.OK);
    }
}