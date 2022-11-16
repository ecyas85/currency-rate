package ru.cbrf.cbrfrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;
import ru.cbrf.cbrfrate.config.props.CbrfProps;
import ru.cbrf.cbrfrate.model.CachedCurrencyRates;
import ru.cbrf.cbrfrate.model.CurrencyRate;
import ru.cbrf.cbrfrate.parser.ICurrencyRateParser;
import ru.cbrf.cbrfrate.requester.ICbrfRequester;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String URL_WITH_PARAMS = "%s?date_req=%s";
    private static final String CURRENCY_RATE_NOT_FOUND_MSG = "Currency rate not found. Currency: '%s', date: '%s'";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CbrfProps cbrfProps;
    private final ICbrfRequester cbrfRequester;
    private final ICurrencyRateParser currencyRateParser;
    private final Cache<LocalDate, CachedCurrencyRates> currencyRatesCache;

    public CurrencyRate getCurrencyRate(String currency, LocalDate date) {
        List<CurrencyRate> currencyRates;

        var cachedCurrencyRates = currencyRatesCache.get(date);
        if (isNull(cachedCurrencyRates)) {
            var urlWithParams = String.format(URL_WITH_PARAMS, cbrfProps.url(), DATE_FORMATTER.format(date));
            var ratesAsXml = cbrfRequester.getRatesAsXml(urlWithParams);
            currencyRates = currencyRateParser.parse(ratesAsXml);
            currencyRatesCache.put(date, new CachedCurrencyRates(currencyRates));
        } else {
            currencyRates = cachedCurrencyRates.currencyRates();
        }
        return currencyRates.stream()
                .filter(currencyRate -> currency.equals(currencyRate.getCharCode()))
                .findFirst()
                .orElseThrow(() -> new CurrencyRateNotFoundException(
                        String.format(CURRENCY_RATE_NOT_FOUND_MSG, currency, date.toString())));
    }
}
