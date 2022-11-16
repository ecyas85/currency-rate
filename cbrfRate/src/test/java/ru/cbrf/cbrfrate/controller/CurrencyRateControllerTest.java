package ru.cbrf.cbrfrate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.cbrf.cbrfrate.config.props.CbrfProps;
import ru.cbrf.cbrfrate.requester.ICbrfRequester;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyRateControllerTest {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String BASE_URL = "/api/v1";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CbrfProps cbrfProps;

    @MockBean
    ICbrfRequester cbrfRequester;

    @Test
    @DirtiesContext
    void getCurrencyRateTest() throws Exception {

        //given
        var currency = "EUR";
        var date = "02-03-2021";
        prepareCbrfRequesterMock(date);

        //when
        var result = webTestClient.get().uri(String.format(BASE_URL + "/currencyRate/%s/%s", currency, date))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockLast();

        // then
        assertEquals("{\"numCode\":\"978\",\"charCode\":\"EUR\",\"nominal\":\"1\",\"name\":\"Евро\",\"value\":\"62,1554\"}", result);
    }

    @Test
    @DirtiesContext
    void cacheUseTest() throws Exception {

        // given
        var currency = "EUR";
        var date = "15-11-2022";
        prepareCbrfRequesterMock(null);

        // when
        webTestClient.get().uri(String.format(BASE_URL + "/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();
        webTestClient.get().uri(String.format(BASE_URL + "/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        currency = "USD";
        webTestClient.get().uri(String.format(BASE_URL + "/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        date = "16-11-2022";
        webTestClient.get().uri(String.format(BASE_URL + "/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        // then
        verify(cbrfRequester, times(2)).getRatesAsXml(any());


    }

    private void prepareCbrfRequesterMock(String date) throws IOException, URISyntaxException {
        var uri = ClassLoader.getSystemResource("cbrf_response.xml").toURI();
        var ratesXml = Files.readString(Paths.get(uri), Charset.forName("Windows-1251"));

        if (date == null) {
            when(cbrfRequester.getRatesAsXml(any())).thenReturn(ratesXml);
        } else {
            var dateParam = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            var cbrUrl =  String.format("%s?date_req=%s", cbrfProps.url(), DATE_FORMATTER.format(dateParam));
            when(cbrfRequester.getRatesAsXml(cbrUrl)).thenReturn(ratesXml);
        }
    }
}