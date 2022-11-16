package ru.cbrf.cbrfrate.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import ru.cbrf.cbrfrate.model.CurrencyRate;
import ru.cbrf.cbrfrate.parser.ICurrencyRateParser;
import ru.cbrf.cbrfrate.parser.exception.CurrencyRateParsingException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyRateParserXml implements ICurrencyRateParser {

    @Override
    public List<CurrencyRate> parse(String rateAsString) {
        var currencyRates = new ArrayList<CurrencyRate>();

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            var db = dbf.newDocumentBuilder();

            try(var reader = new StringReader(rateAsString)) {
                var doc = db.parse(new InputSource(reader));
                doc.getDocumentElement().normalize();
                var valuteNodes = doc.getElementsByTagName("Valute");
                for (int valuteNodeIdx = 0; valuteNodeIdx < valuteNodes.getLength(); valuteNodeIdx++) {
                    var valuteNode = valuteNodes.item(valuteNodeIdx);
                    if (Node.ELEMENT_NODE == valuteNode.getNodeType()) {
                        var element = (Element) valuteNode;
                        var currencyRate = CurrencyRate.builder()
                                .numCode(element.getElementsByTagName("NumCode").item(0).getTextContent())
                                .charCode(element.getElementsByTagName("CharCode").item(0).getTextContent())
                                .nominal(element.getElementsByTagName("Nominal").item(0).getTextContent())
                                .name(element.getElementsByTagName("Name").item(0).getTextContent())
                                .value(element.getElementsByTagName("Value").item(0).getTextContent())
                                .build();
                        currencyRates.add(currencyRate);
                    }
                }
            }
        } catch (Exception e) {
            log.error("XML parsing error, xml:{}", rateAsString, e);
            throw new CurrencyRateParsingException(e);
        }
        return currencyRates;
    }
}
