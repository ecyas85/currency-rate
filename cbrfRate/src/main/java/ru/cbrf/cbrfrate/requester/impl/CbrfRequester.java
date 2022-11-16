package ru.cbrf.cbrfrate.requester.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cbrf.cbrfrate.requester.ICbrfRequester;
import ru.cbrf.cbrfrate.requester.RequesterException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class CbrfRequester implements ICbrfRequester {

    @Override
    public String getRatesAsXml(String url) {
        try {
            log.info("Request for url:{}", url);
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Cbrf request error, url:{}", url, e);
            throw new RequesterException(e);
        }
    }
}
