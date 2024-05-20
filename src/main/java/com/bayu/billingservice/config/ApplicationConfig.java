package com.bayu.billingservice.config;

import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.model.Customer;
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
        modelMapper.getConfiguration().isSkipNullEnabled();

        // Konfigurasi konversi kustom untuk String to BigDecimal
        modelMapper.addConverter(stringToBigDecimalConverter());

        // Konfigurasi konversi kustom untuk BigDecimal to formatted String
        // modelMapper.addConverter(bigDecimalToStringConverter());

//        modelMapper.typeMap(CustomerDTO.class, Customer.class)
//                .addMappings(mapper -> mapper.map(
//                        src -> src.getGl() != null ? src.getGl() : null,
//                        Customer::setGl
//                ));
//
//        modelMapper.typeMap(Customer.class, CustomerDTO.class)
//                .addMappings(mapper -> mapper.map(
//                        Customer::isGl,
//                        (dest, value) -> dest.setGl(value != null ? value.toString() : null)
//                ));

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

    @Bean
    public Converter<Boolean, Boolean> booleanConverter() {
        return context -> {
            // Memeriksa jika nilai dari DTO adalah null
            if (context.getSource() == null) {
                // Mengembalikan nilai dari database jika nilai DTO adalah null
                return context.getDestination();
            }
            // Mengembalikan nilai dari DTO jika tidak null
            return context.getSource();
        };
    }
}
