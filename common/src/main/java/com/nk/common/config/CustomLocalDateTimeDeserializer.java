package com.nk.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nk.base.exception.BadRequestException;
import com.nk.common.constants.CommonConstants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter;

    public CustomLocalDateTimeDeserializer(DateTimeFormatter dateFormatter) {
        this.formatter = dateFormatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            String dateString = parser.getText();
            return LocalDateTime.parse(dateString, formatter);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid input value for date. Expected a string field with format " + CommonConstants.DATE_TIME_FORMATE);
        }
    }
}