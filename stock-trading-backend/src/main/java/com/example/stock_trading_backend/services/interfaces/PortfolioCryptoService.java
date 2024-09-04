package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.entities.PortfolioCrypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

import java.util.List;

public interface PortfolioCryptoService {
    PortfolioCrypto createPortfolioCrypto(PortfolioCrypto portfolioCrypto);
    PortfolioCrypto getPortfolioCryptoById(Long id) throws EntityNotFoundException;
    void deletePortfolioCrypto(Long id) throws EntityNotFoundException;
    List<PortfolioCrypto> getPortfolioCryptosByPortfolioId(Long portfolioId);
}
