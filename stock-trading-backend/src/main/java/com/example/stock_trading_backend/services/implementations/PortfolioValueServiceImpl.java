package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.entities.PortfolioCrypto;
import com.example.stock_trading_backend.entities.PortfolioValue;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.CryptoRepository;
import com.example.stock_trading_backend.repositories.PortfolioRepository;
import com.example.stock_trading_backend.repositories.PortfolioValueRepository;
import com.example.stock_trading_backend.services.interfaces.PortfolioValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioValueServiceImpl implements PortfolioValueService {

    @Autowired
    private PortfolioValueRepository portfolioValueRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private CryptoRepository cryptoRepository;

    @Override
    public PortfolioValue createPortfolioValue(PortfolioValue portfolioValue) {
        return portfolioValueRepository.save(portfolioValue);
    }

    @Override
    public PortfolioValue getPortfolioValueById(Long id) throws EntityNotFoundException {
        return portfolioValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PortfolioValue not found with id " + id));
    }

    @Override
    public PortfolioValue updatePortfolioValue(Long id, PortfolioValue portfolioValue) throws EntityNotFoundException {
        PortfolioValue existingPortfolioValue = portfolioValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PortfolioValue not found with id " + id));

        existingPortfolioValue.setTotalValue(portfolioValue.getTotalValue());
        existingPortfolioValue.setDate(portfolioValue.getDate());

        return portfolioValueRepository.save(existingPortfolioValue);
    }

    @Override
    public void deletePortfolioValue(Long id) throws EntityNotFoundException {
        PortfolioValue existingPortfolioValue = portfolioValueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PortfolioValue not found with id " + id));
        portfolioValueRepository.delete(existingPortfolioValue);
    }

    @Override
    public List<PortfolioValue> getPortfolioValuesByPortfolioId(Long portfolioId) {
        return portfolioValueRepository.findByPortfolioId(portfolioId);
    }

    @Override
    public List<PortfolioValue> getPortfolioValuesByDateRange(Long portfolioId, LocalDateTime startDate, LocalDateTime endDate) {
        return portfolioValueRepository.findByPortfolioIdAndDateBetween(portfolioId, startDate, endDate);
    }


    @Transactional
    public void calculatePortfolioValues() {
        List<Portfolio> portfolios = portfolioRepository.findAll();

        for (Portfolio portfolio : portfolios) {
            Double totalValue = 0.0;

            // Assuming Portfolio has a list of PortfolioCrypto entities
            for (PortfolioCrypto portfolioCrypto : portfolio.getListPortfolioCryptos()) {
                Optional<Crypto> tmpCrypto = cryptoRepository.findByAssetId(portfolioCrypto.getCrypto().getAssetId());

                if (tmpCrypto.isPresent()) {
                    Double cryptoPrice = tmpCrypto.get().getPriceUsd();
                    double cryptoValue = cryptoPrice * portfolioCrypto.getQuantity();
                    totalValue = totalValue + cryptoValue;
                }
            }

            // Save the calculated value
            PortfolioValue portfolioValue = new PortfolioValue();
            portfolioValue.setPortfolio(portfolio);
            portfolioValue.setDate(LocalDateTime.now());
            portfolioValue.setTotalValue(totalValue);

            portfolioValueRepository.save(portfolioValue);
        }
        }
    }
