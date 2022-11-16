package ru.cbrf.cbrfrate.parser;

import org.junit.jupiter.api.Test;
import ru.cbrf.cbrfrate.model.CurrencyRate;
import ru.cbrf.cbrfrate.parser.impl.CurrencyRateParserXml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyRateParserXmlTest {

    @Test
    void parseTest() throws URISyntaxException, IOException {
        // given
        var parser = new CurrencyRateParserXml();
        var uri = ClassLoader.getSystemResource("cbrf_response.xml").toURI();
        var ratesXml = Files.readString(Paths.get(uri), Charset.forName("Windows-1251"));

        // when
        var rates = parser.parse(ratesXml);

        // then
        assertEquals(34, rates.size());
        assertTrue(rates.contains(getUSDRate()));
        assertTrue(rates.contains(getEURRate()));
        assertTrue(rates.contains(getJPYRate()));
    }

    CurrencyRate getUSDRate() {
        return CurrencyRate.builder()
                .numCode("840")
                .charCode("USD")
                .nominal("1")
                .name("Доллар США")
                .value("60,3982")
                .build();
    }

    CurrencyRate getEURRate() {
        return CurrencyRate.builder()
                .numCode("978")
                .charCode("EUR")
                .nominal("1")
                .name("Евро")
                .value("62,1554")
                .build();
    }

    CurrencyRate getJPYRate() {
        return CurrencyRate.builder()
                .numCode("392")
                .charCode("JPY")
                .nominal("100")
                .name("Японских иен")
                .value("43,2652")
                .build();
    }

}