package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.entities.Transaction;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.CryptoRepository;
import com.example.stock_trading_backend.repositories.PortfolioRepository;
import com.example.stock_trading_backend.repositories.TransactionRepository;
import com.example.stock_trading_backend.services.interfaces.PortfolioService;
import com.example.stock_trading_backend.services.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private CryptoRepository cryptoRepository;

    @Override
    public Transaction createTransaction(Transaction transaction) throws EntityNotFoundException {
        Portfolio existingPortfolio = portfolioRepository.findById(transaction.getPortfolio().getId())
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
        Crypto existingCrypto = cryptoRepository.findById(transaction.getCrypto().getId())
                .orElseThrow(() -> new EntityNotFoundException("Crypto not found"));

        transaction.setPortfolio(existingPortfolio);
        transaction.setCrypto(existingCrypto);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) throws EntityNotFoundException {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id " + id));
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction, String type) throws EntityNotFoundException {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id " + id));

        existingTransaction.setPortfolio(transaction.getPortfolio());
        existingTransaction.setCrypto(transaction.getCrypto());
        existingTransaction.setQuantity(transaction.getQuantity());
        existingTransaction.setPrice(transaction.getPrice());
        existingTransaction.setTransactionDate(transaction.getTransactionDate());
        existingTransaction.setType(type.equals("BUY")? Transaction.TransactionType.BUY : Transaction.TransactionType.SELL);

        return transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteTransaction(Long id) throws EntityNotFoundException {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id " + id));
        transactionRepository.delete(existingTransaction);
    }

    @Override
    public List<Transaction> getTransactionsByPortfolioId(Long portfolioId) {
        return transactionRepository.findByPortfolioId(portfolioId);
    }

    @Override
    public List<Transaction> getTransactionsByCryptoId(Long stockId) {
        return transactionRepository.findByCryptoId(stockId);
    }
}

