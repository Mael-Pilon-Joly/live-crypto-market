package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {
    Optional<Crypto> findByAssetId(String assetId);
}
