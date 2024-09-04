package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.entities.Transaction;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction) throws EntityNotFoundException;
    Transaction getTransactionById(Long id) throws EntityNotFoundException;
    Transaction updateTransaction(Long id, Transaction transaction, String type) throws EntityNotFoundException;
    void deleteTransaction(Long id) throws EntityNotFoundException;
    List<Transaction> getTransactionsByPortfolioId(Long portfolioId);
    List<Transaction> getTransactionsByCryptoId(Long cryptoId);
}
