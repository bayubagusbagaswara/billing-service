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

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().isSkipNullEnabled();

        modelMapper.addConverter(stringToBigDecimalConverter());

        return modelMapper;
    }

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
        return new AbstractConverter<>() {
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

    @Bean
    public Converter<Boolean, Boolean> booleanConverter() {
        return context -> {
            if (context.getSource() == null) {
                return context.getDestination();
            }
            return context.getSource();
        };
    }

    @Bean
    public Converter<String, String> nullToEmptyStringConverter() {
        return context -> context.getSource() == null ? "" : context.getSource();
    }
}
