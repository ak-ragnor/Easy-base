package com.easyBase.web.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

    /**
     * Custom serializer for ZonedDateTime that respects configured timezone
     */
    public class CustomZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

        private final String timezone;
        private final DateTimeFormatter formatter;

        public CustomZonedDateTimeSerializer(DateTimeFormatter formatter, String timezone) {
            this.formatter = formatter;
            this.timezone = timezone;
        }

        @Override
        public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value != null) {
                ZonedDateTime zonedValue = value.withZoneSameInstant(ZoneId.of(timezone));
                gen.writeString(formatter.format(zonedValue));
            }
        }
    }