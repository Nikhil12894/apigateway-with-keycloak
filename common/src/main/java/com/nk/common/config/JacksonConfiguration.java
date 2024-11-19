package com.nk.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import com.nk.common.constants.CommonConstants;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        return builder -> {

            // formatter
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMATE);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_TIME_FORMATE);

            // deserializers
            builder.deserializerByType(LocalDate.class, new CustomLocalDateDeserializer(dateFormatter));
            builder.deserializerByType(LocalDateTime.class, new CustomLocalDateTimeDeserializer(dateTimeFormatter));

            // serializers
            builder.serializers(new LocalDateSerializer(dateFormatter));
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
            builder.modulesToInstall(new JavaTimeModule());
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Register JavaTimeModule for LocalDate handling
        objectMapper.registerModule(new JavaTimeModule());

        // If you need custom LocalDate serialization/deserialization, you can create a
        // SimpleModule:
        SimpleModule localDateModule = new SimpleModule();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMATE);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_TIME_FORMATE);
        localDateModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer(dateFormatter));
        localDateModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer(dateTimeFormatter));
        localDateModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        localDateModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        objectMapper.registerModule(localDateModule);

        return objectMapper;
    }
}