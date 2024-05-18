package com.bayu.billingservice.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Konfigurasi matching strategies
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Konfigurasi konversi kustom untuk String to BigDecimal
        modelMapper.addConverter(stringToBigDecimalConverter());

        // Konfigurasi konversi kustom untuk BigDecimal to formatted String
        // modelMapper.addConverter(bigDecimalToStringConverter());

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

    @Bean
    public Converter<BigDecimal, String> bigDecimalToStringConverter() {
        return new AbstractConverter<BigDecimal, String>() {
            protected String convert(BigDecimal value) {
                if (BigDecimal.ZERO.compareTo(value) == 0) {
                    return "0";
                } else {
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                    symbols.setGroupingSeparator(',');
                    symbols.setDecimalSeparator('.');

                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

                    return decimalFormat.format(value);
                }
            }
        };
    }
}
