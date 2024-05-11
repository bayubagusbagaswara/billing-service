package com.bayu.billingservice.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Konfigurasi matching strategies
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Konfigurasi konversi kustom untuk String to BigDecimal
        modelMapper.addConverter(stringToBigDecimalConverter());

        return modelMapper;
    }

    // Konverter kustom untuk String to BigDecimal
    @Bean
    public Converter<String, BigDecimal> stringToBigDecimalConverter() {
        return new AbstractConverter<String, BigDecimal>() {
            protected BigDecimal convert(String source) {
                return source != null ? new BigDecimal(source) : null;
            }
        };
    }
}
