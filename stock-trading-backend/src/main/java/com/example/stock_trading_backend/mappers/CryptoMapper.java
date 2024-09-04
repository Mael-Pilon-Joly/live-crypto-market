package com.example.stock_trading_backend.mappers;

import com.example.stock_trading_backend.dto.CryptoDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)

@Component
public class CryptoMapper {
    private final ObjectMapper objectMapper;

    public CryptoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CryptoDto> convertJsonToCryptoDtoList(String jsonResponse) {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try {
            List<CryptoDto> cryptoDtoList = objectMapper.readValue(jsonResponse, new TypeReference<List<CryptoDto>>() {});
            // Filter out objects with missing required fields
            return cryptoDtoList.stream()
                    .filter(dto -> dto.getAssetId() != null &&
                            dto.getName() != null &&
                            dto.getDataQuoteStart() != null &&
                            dto.getDataQuoteEnd() != null &&
                            dto.getDataTradeStart() != null &&
                            dto.getDataTradeEnd() != null &&
                            dto.getPriceUsd() > 0
                            )
                    .collect(Collectors.toList());        } catch (Exception e) {
            throw new RuntimeException("Error while mapping JSON to CryptoDto list", e);
        }
    }

}
