package ru.cbrf.cbrfrate.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cbrf")
public record CbrfProps(String url) {
}

