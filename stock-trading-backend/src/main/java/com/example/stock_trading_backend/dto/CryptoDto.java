package com.example.stock_trading_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoDto {

    private String assetId;
    private String name;
    private int typeIsCrypto;
    private String dataQuoteStart;
    private String dataQuoteEnd;
    private String dataOrderbookStart;
    private String dataOrderbookEnd;
    private String dataTradeStart;
    private String dataTradeEnd;
    private int dataSymbolsCount;
    private double volume_1hrsUsd;
    private double volume_1dayUsd;
    private double volume_1mthUsd;
    private double priceUsd;
    private String dataStart;
    private String dataEnd;
}
