package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.dto.CryptoDto;
import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

import java.util.List;

public interface CryptoService {
    Crypto createCrypto(Crypto crypto);
    Crypto getCryptoById(Long id) throws EntityNotFoundException;
    List<Crypto> getAllCryptos();
    Crypto updateCrypto(Long id, Crypto crypto) throws EntityNotFoundException;
    void deleteCrypto(Long id) throws EntityNotFoundException;
    Crypto getCryptoByAssetId(String symbol) throws EntityNotFoundException;
    void updateCryptoData(List<CryptoDto> cryptoDtoList);
}
