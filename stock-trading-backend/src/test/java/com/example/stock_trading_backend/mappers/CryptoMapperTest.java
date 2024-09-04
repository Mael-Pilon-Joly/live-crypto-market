package com.example.stock_trading_backend.mappers;

import com.example.stock_trading_backend.dto.CryptoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CryptoMapperTest {

    private CryptoMapper cryptoMapper;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        cryptoMapper = new CryptoMapper(objectMapper);
    }

    @Test
    public void testConvertJsonToCryptoDtoList() {
        String jsonResponse = "[\n" +
        "  {\n" +
                "    \"asset_id\": \"AUD\",\n" +
                "    \"name\": \"Australian Dollar\",\n" +
                "    \"type_is_crypto\": 0,\n" +
                "    \"data_quote_start\": \"2017-03-19T00:00:00.0000000Z\",\n" +
                "    \"data_quote_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_orderbook_start\": \"2017-03-18T22:54:11.8228879Z\",\n" +
                "    \"data_orderbook_end\": \"2023-07-07T00:00:00.0000000Z\",\n" +
                "    \"data_trade_start\": \"2011-09-02T00:00:00.0000000Z\",\n" +
                "    \"data_trade_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_symbols_count\": 479,\n" +
                "    \"volume_1hrs_usd\": 0,\n" +
                "    \"volume_1day_usd\": 0.34,\n" +
                "    \"volume_1mth_usd\": 33291335.19,\n" +
                "    \"price_usd\": 0.6776563366848728053998247089,\n" +
                "    \"id_icon\": \"c33d154f-14df-49b6-b734-6c4f3034e548\",\n" +
                "    \"data_start\": \"2011-09-02\",\n" +
                "    \"data_end\": \"2024-08-30\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"asset_id\": \"CHF\",\n" +
                "    \"name\": \"Swiss Franc\",\n" +
                "    \"type_is_crypto\": 0,\n" +
                "    \"data_quote_start\": \"2017-03-15T00:00:00.0000000Z\",\n" +
                "    \"data_quote_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_orderbook_start\": \"2017-03-14T20:05:32.5680571Z\",\n" +
                "    \"data_orderbook_end\": \"2023-07-07T00:00:00.0000000Z\",\n" +
                "    \"data_trade_start\": \"2011-09-03T00:00:00.0000000Z\",\n" +
                "    \"data_trade_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_symbols_count\": 277,\n" +
                "    \"volume_1hrs_usd\": 40759.02,\n" +
                "    \"volume_1day_usd\": 901700.33,\n" +
                "    \"volume_1mth_usd\": 71672344.86,\n" +
                "    \"price_usd\": 1.1764770326074618166602817978,\n" +
                "    \"data_start\": \"2011-09-03\",\n" +
                "    \"data_end\": \"2024-08-30\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"asset_id\": \"SEK\",\n" +
                "    \"name\": \"Swedish Krona\",\n" +
                "    \"type_is_crypto\": 0,\n" +
                "    \"data_quote_start\": \"2017-03-19T00:00:00.0000000Z\",\n" +
                "    \"data_quote_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_orderbook_start\": \"2017-03-18T22:53:42.9249239Z\",\n" +
                "    \"data_orderbook_end\": \"2023-07-06T00:00:00.0000000Z\",\n" +
                "    \"data_trade_start\": \"2011-09-04T00:00:00.0000000Z\",\n" +
                "    \"data_trade_end\": \"2024-08-30T00:00:00.0000000Z\",\n" +
                "    \"data_symbols_count\": 53,\n" +
                "    \"volume_1hrs_usd\": 0,\n" +
                "    \"volume_1day_usd\": 0.06,\n" +
                "    \"volume_1mth_usd\": 0,\n" +
                "    \"price_usd\": 0.0974502751912144290134757406,\n" +
                "    \"data_start\": \"2011-09-04\",\n" +
                "    \"data_end\": \"2024-08-30\"\n" +
                "  }\n" +
                "]";

        List<CryptoDto> cryptoDtoList = cryptoMapper.convertJsonToCryptoDtoList(jsonResponse);

        // Assert the size of the filtered list
        assertEquals(3, cryptoDtoList.size());

        CryptoDto cryptoToTest = cryptoDtoList.get(0);

        assertNotEquals(cryptoToTest.getVolume_1dayUsd(), 0);
    }
}