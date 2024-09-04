package com.example.stock_trading_backend.controllers;


import com.example.stock_trading_backend.entities.Transaction;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.services.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) throws EntityNotFoundException {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) throws EntityNotFoundException {
        Transaction transaction = transactionService.getTransactionById(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @PathVariable String type, @RequestBody Transaction transaction) throws EntityNotFoundException {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction, type);
        return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) throws EntityNotFoundException {
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolioId(@PathVariable Long portfolioId) {
        List<Transaction> transactions = transactionService.getTransactionsByPortfolioId(portfolioId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/stock/{stockId}")
    public ResponseEntity<List<Transaction>> getTransactionsByStockId(@PathVariable Long stockId) {
        List<Transaction> transactions = transactionService.getTransactionsByCryptoId(stockId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}