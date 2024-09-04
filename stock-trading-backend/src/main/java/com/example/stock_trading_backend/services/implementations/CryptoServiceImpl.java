package com.example.stock_trading_backend.services.implementations;

import com.example.stock_trading_backend.dto.CryptoDto;
import com.example.stock_trading_backend.entities.Crypto;
import com.example.stock_trading_backend.entities.CryptoHistory;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.mappers.CryptoMapper;
import com.example.stock_trading_backend.repositories.CryptoHistoryRepository;
import com.example.stock_trading_backend.repositories.CryptoRepository;
import com.example.stock_trading_backend.services.interfaces.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CryptoServiceImpl implements CryptoService {

    @Value("${coin.api.key}")
    private String COIN_API_KEY;

    private static final String COIN_API_URL = "https://rest.coinapi.io/v1/assets";

    private final CryptoRepository cryptoRepository;
    private final CryptoMapper cryptoMapper;
    private final CryptoHistoryRepository cryptoHistoryRepository;

    @Autowired
    public CryptoServiceImpl(CryptoRepository cryptoRepository, CryptoMapper cryptoMapper, CryptoHistoryRepository cryptoHistoryRepository) {
        this.cryptoRepository = cryptoRepository;
        this.cryptoMapper = cryptoMapper;
        this.cryptoHistoryRepository = cryptoHistoryRepository;
    }

    @Override
    public Crypto createCrypto(Crypto crypto) {
        return cryptoRepository.save(crypto);
    }

    @Override
    public Crypto getCryptoById(Long id) throws EntityNotFoundException {
        return cryptoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id " + id));
    }

    @Override
    public List<Crypto> getAllCryptos() {
        return cryptoRepository.findAll();
    }

    @Override
    public Crypto updateCrypto(Long id, Crypto crypto) throws EntityNotFoundException {
        Crypto existingCrypto = cryptoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id " + id));

        existingCrypto.setDataQuoteStart(crypto.getDataQuoteStart());
        existingCrypto.setDataQuoteEnd(crypto.getDataQuoteEnd());
        existingCrypto.setDataTradeStart((crypto.getDataTradeStart()));
        existingCrypto.setDataTradeEnd(crypto.getDataTradeEnd());
        existingCrypto.setPriceUsd(crypto.getPriceUsd());
        existingCrypto.setVolume1dayUsd(crypto.getVolume1dayUsd());
        existingCrypto.setVolume1hrsUsd(crypto.getVolume1hrsUsd());
        existingCrypto.setVolume1mthUsd(crypto.getVolume1mthUsd());

        return cryptoRepository.save(existingCrypto);
    }

    @Override
    public void deleteCrypto(Long id) throws EntityNotFoundException {
        Crypto existingCrypto = cryptoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Crypto not found with id " + id));

        cryptoRepository.delete(existingCrypto);
    }

    @Override
    public Crypto getCryptoByAssetId(String assetId) throws EntityNotFoundException {
        return cryptoRepository.findByAssetId(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Crypto not found with assetId " + assetId));
    }

    @Override
    public void updateCryptoData(List<CryptoDto> cryptoDtoList) {

        for (CryptoDto dto : cryptoDtoList) {
            // Fetch the existing Crypto entity
            Optional<Crypto> optionalCrypto = cryptoRepository.findByAssetId(dto.getAssetId());

            Crypto crypto;
            if (optionalCrypto.isPresent()) {
                // Update the existing entity
                crypto = optionalCrypto.get();
            } else {
                // Create a new entity
                crypto = new Crypto();
                crypto.setAssetId(dto.getAssetId());
            }

            // Update or set other fields
            crypto.setName(dto.getName());
            crypto.setTypeIsCrypto(dto.getTypeIsCrypto() == 1);
            crypto.setDataQuoteStart(dto.getDataQuoteStart());
            crypto.setDataQuoteEnd(dto.getDataQuoteEnd());
            crypto.setDataTradeStart(dto.getDataTradeStart());
            crypto.setDataTradeEnd(dto.getDataTradeEnd());
            crypto.setVolume1hrsUsd(dto.getVolume_1hrsUsd());
            crypto.setVolume1dayUsd(dto.getVolume_1dayUsd());
            crypto.setVolume1mthUsd(dto.getVolume_1mthUsd());
            crypto.setPriceUsd(dto.getPriceUsd());
            // Save or update the entity
            cryptoRepository.save(crypto);
        }
    }

    public void updateCryptoHistoryData(List<CryptoDto> cryptoDtoList) {

        for (CryptoDto dto : cryptoDtoList) {
            CryptoHistory cryptoHistory = new CryptoHistory();

            cryptoHistory.setTimestamp(LocalDateTime.now());
            cryptoHistory.setAssetId(dto.getAssetId());
            cryptoHistory.setPriceUsd(dto.getPriceUsd());
            cryptoHistory.setVolume_1hrsUsd(dto.getVolume_1hrsUsd());
            cryptoHistory.setVolume_1dayUsd(dto.getVolume_1dayUsd());
            cryptoHistory.setVolume_1mthUsd(dto.getVolume_1mthUsd());

            cryptoHistoryRepository.save(cryptoHistory);
        }


    }

    public void getCryptosFromApiAndSaveToDb() {
        RestTemplate restTemplate = new RestTemplate();
        System.out.print("Coin API key is: " + COIN_API_KEY);
        String jsonResponse = restTemplate.getForObject(COIN_API_URL + "?apikey=" + COIN_API_KEY, String.class);

        List<CryptoDto> cryptosToDTO =  cryptoMapper.convertJsonToCryptoDtoList(jsonResponse);

        updateCryptoData(cryptosToDTO);

        updateCryptoHistoryData(cryptosToDTO);
    }

}
