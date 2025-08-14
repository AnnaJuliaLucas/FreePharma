package com.annaehugo.freepharma.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configuração geral - compatível com Java 17+
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(false) // Desabilitar acesso a campos privados
                .setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
                .setSkipNullEnabled(true);
        
        // Converter personalizado para BigDecimal
        Converter<String, BigDecimal> stringToBigDecimal = new Converter<String, BigDecimal>() {
            @Override
            public BigDecimal convert(MappingContext<String, BigDecimal> context) {
                String source = context.getSource();
                if (source == null || source.trim().isEmpty()) {
                    return BigDecimal.ZERO;
                }
                try {
                    return new BigDecimal(source);
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            }
        };
        
        // Converter personalizado para BigDecimal para String
        Converter<BigDecimal, String> bigDecimalToString = new Converter<BigDecimal, String>() {
            @Override
            public String convert(MappingContext<BigDecimal, String> context) {
                BigDecimal source = context.getSource();
                return source != null ? source.toString() : "0";
            }
        };
        
        // Converter para garantir que BigDecimal null seja convertido para BigDecimal.ZERO
        Converter<BigDecimal, BigDecimal> bigDecimalToBigDecimal = new Converter<BigDecimal, BigDecimal>() {
            @Override
            public BigDecimal convert(MappingContext<BigDecimal, BigDecimal> context) {
                BigDecimal source = context.getSource();
                return source != null ? source : BigDecimal.ZERO;
            }
        };
        
        // Converter para Double para BigDecimal
        Converter<Double, BigDecimal> doubleToBigDecimal = new Converter<Double, BigDecimal>() {
            @Override
            public BigDecimal convert(MappingContext<Double, BigDecimal> context) {
                Double source = context.getSource();
                if (source == null) {
                    return BigDecimal.ZERO;
                }
                return BigDecimal.valueOf(source);
            }
        };
        
        // Converter para Integer para BigDecimal
        Converter<Integer, BigDecimal> integerToBigDecimal = new Converter<Integer, BigDecimal>() {
            @Override
            public BigDecimal convert(MappingContext<Integer, BigDecimal> context) {
                Integer source = context.getSource();
                if (source == null) {
                    return BigDecimal.ZERO;
                }
                return BigDecimal.valueOf(source);
            }
        };
        
        // Registrar os conversores
        mapper.addConverter(stringToBigDecimal);
        mapper.addConverter(bigDecimalToString);
        mapper.addConverter(bigDecimalToBigDecimal);
        mapper.addConverter(doubleToBigDecimal);
        mapper.addConverter(integerToBigDecimal);
        
        return mapper;
    }
}