package com.easyBase.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * Custom deserializer for ZonedDateTime that adjusts to a configured timezone
 */
public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    private final ZoneId zoneId;
    private final DateTimeFormatter formatter;

    public CustomZonedDateTimeDeserializer(DateTimeFormatter formatter, String timezone) {
        if (timezone == null || timezone.isBlank()) {
            throw new IllegalArgumentException("Timezone cannot be null or empty");
        }
        try {
            this.zoneId = ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone, e);
        }
        this.formatter = formatter;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        ZonedDateTime parsed = ZonedDateTime.parse(dateString, formatter);
        return parsed.withZoneSameInstant(zoneId);
    }
}