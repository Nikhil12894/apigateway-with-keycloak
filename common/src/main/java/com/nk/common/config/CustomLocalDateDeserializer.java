package com.nk.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.nk.common.constants.CommonConstants;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private final DateTimeFormatter formatter;

    public CustomLocalDateDeserializer(DateTimeFormatter dateFormatter) {
        this.formatter = dateFormatter;
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            String dateString = parser.getText();
            return LocalDate.parse(dateString, formatter);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid input value for date. Expected a string field with format " + CommonConstants.DATE_TIME_FORMATE);
        }
    }
}