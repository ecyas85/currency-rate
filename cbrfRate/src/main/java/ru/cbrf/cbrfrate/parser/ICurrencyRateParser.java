package ru.cbrf.cbrfrate.parser;

import ru.cbrf.cbrfrate.model.CurrencyRate;

import java.util.List;

public interface ICurrencyRateParser {

    List<CurrencyRate> parse(String rateAsString);
}
