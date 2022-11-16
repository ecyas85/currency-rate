package ru.cbrf.cbrfrate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cbrf.cbrfrate.model.CurrencyRate;
import ru.cbrf.cbrfrate.service.CurrencyRateService;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${app.rest.api.prefix}/v1")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping("/currencyRate/{currency}/{date}")
    public CurrencyRate getCurrencyRate(@PathVariable String currency,
                                        @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable LocalDate date) {
      log.info("CurrencyRateController: try to get currency rate for currency:'{}' on {} date", currency, date.toString());

      var rate = currencyRateService.getCurrencyRate(currency, date);
      log.info("Rate for {}:{}", rate.getCharCode(), rate.getValue());
      return rate;
    }
}
