package com.bayu.billingservice.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().isSkipNullEnabled();

        modelMapper.addConverter(stringToBigDecimalConverter());
        modelMapper.addConverter(bigDecimalToStringConverter());
        modelMapper.addConverter(stringToLocalDateConverter());

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
                    BigDecimal bigDecimal = value.stripTrailingZeros();
                    return bigDecimal.toPlainString();
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

    @Bean
    public Converter<String, LocalDate> stringToLocalDateConverter() {
        return new  AbstractConverter<String, LocalDate>() {
            @Override
            protected LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ISO_DATE);
            }
        };
    }


}
