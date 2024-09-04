package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.entities.PortfolioCrypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.CryptoRepository;
import com.example.stock_trading_backend.repositories.PortfolioRepository;
import com.example.stock_trading_backend.services.interfaces.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private CryptoRepository cryptoRepository;

    @Override
    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio getPortfolioById(Long id) throws EntityNotFoundException {
        return portfolioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id " + id));
    }

    @Override
    public Portfolio updatePortfolio(Long id, Portfolio portfolio) throws EntityNotFoundException{
        Portfolio existingPortfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id " + id));
        existingPortfolio.setName(portfolio.getName());
        return portfolioRepository.save(existingPortfolio);
    }

    @Override
    public void deletePortfolio(Long id) throws EntityNotFoundException {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id " + id));
        portfolioRepository.delete(portfolio);
    }

    public double getCurrentPortfolioWorth(Long id) throws EntityNotFoundException {
        Portfolio portfolio = portfolioRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id " + id));

        double portfolioTotalWorth = 0;

        for (PortfolioCrypto portfolioCrypto: portfolio.getListPortfolioCryptos()) {
            Crypto crypto = cryptoRepository.findById(portfolioCrypto.getCrypto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Crypto not found with id "+ id));

            portfolioTotalWorth += crypto.getPriceUsd() * portfolioCrypto.getQuantity();
        }

        return portfolioTotalWorth;
    }

}
