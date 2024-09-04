package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.CryptoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoHistoryRepository extends JpaRepository<CryptoHistory, Long> {
    List<CryptoHistory> findByAssetIdOrderByTimestampDesc(String assetId);
    @Query("SELECT ch FROM CryptoHistory ch WHERE ch.timestamp >= :fromTime ORDER BY ch.timestamp ASC")
    List<CryptoHistory> findCryptoHistoryFrom(@Param("fromTime") LocalDateTime fromTime);

    @Query("SELECT ch FROM CryptoHistory ch WHERE ch.assetId = :assetId AND ch.timestamp >= :fromTime ORDER BY ch.timestamp ASC")
    List<CryptoHistory> findCryptoHistoryForLast4Hours(@Param("assetId") String assetId, @Param("fromTime") LocalDateTime fromTime);
}
