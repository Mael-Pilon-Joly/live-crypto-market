package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.entities.PortfolioCrypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.PortfolioCryptoRepository;
import com.example.stock_trading_backend.services.interfaces.PortfolioCryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioCryptoServiceImpl implements PortfolioCryptoService {

    @Autowired
    private PortfolioCryptoRepository portfolioCryptoRepository;

    @Override
    public PortfolioCrypto createPortfolioCrypto(PortfolioCrypto portfolioCrypto) {
        return portfolioCryptoRepository.save(portfolioCrypto);
    }

    @Override
    public PortfolioCrypto getPortfolioCryptoById(Long id) throws EntityNotFoundException {

        return portfolioCryptoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PortfolioCrypto not found with id " + id));
    }

    @Override
    public void deletePortfolioCrypto(Long id) throws EntityNotFoundException {
        PortfolioCrypto existingPortfolioCrypto = portfolioCryptoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PortfolioCrypto not found with id " + id));

        portfolioCryptoRepository.delete(existingPortfolioCrypto);
    }

    @Override
    public List<PortfolioCrypto> getPortfolioCryptosByPortfolioId(Long portfolioId) {
      return portfolioCryptoRepository.findByPortfolioId(portfolioId);
    }
}
