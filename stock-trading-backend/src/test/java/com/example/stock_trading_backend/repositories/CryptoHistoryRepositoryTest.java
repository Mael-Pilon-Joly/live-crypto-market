package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.CryptoHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")  // Ensure the test profile is used, which is configured with the in-memory database
public class CryptoHistoryRepositoryTest {

    @Autowired
    private CryptoHistoryRepository cryptoHistoryRepository;

    @BeforeEach
    public void setUp() {
        // Create and save some test data
        CryptoHistory history1 = new CryptoHistory();
        history1.setAssetId("XHV");
        history1.setTimestamp(LocalDateTime.now().minusHours(2));  // Within the last 4 hours
        history1.setPriceUsd(200.0);
        history1.setVolume_1hrsUsd(1000.0);
        cryptoHistoryRepository.save(history1);

        CryptoHistory history2 = new CryptoHistory();
        history2.setAssetId("XHV");
        history2.setTimestamp(LocalDateTime.now().minusHours(6));  // Outside the last 4 hours
        history2.setPriceUsd(180.0);
        history2.setVolume_1hrsUsd(1500.0);
        cryptoHistoryRepository.save(history2);

        CryptoHistory history3 = new CryptoHistory();
        history3.setAssetId("BTC");
        history3.setTimestamp(LocalDateTime.now().minusHours(1));  // Within the last 4 hours but different assetId
        history3.setPriceUsd(50000.0);
        history3.setVolume_1hrsUsd(3000.0);
        cryptoHistoryRepository.save(history3);
    }

    @Test
    public void testFindCryptoHistoryForLast4Hours() {
        // Given
        String assetId = "XHV";
        LocalDateTime fromTime = LocalDateTime.now().minusHours(4);

        // When
        List<CryptoHistory> result = cryptoHistoryRepository.findCryptoHistoryForLast4Hours(assetId, fromTime);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);  // Only one record should match
        assertThat(result.get(0).getAssetId()).isEqualTo("XHV");
        assertThat(result.get(0).getTimestamp()).isAfterOrEqualTo(fromTime);
    }
}
