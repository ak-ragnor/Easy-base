package com.easyBase.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Centralized service for handling timezone conversions
 * This ensures consistent timezone handling across the application
 */
@Service
public class TimezoneService {

    @Value("${app.timezone:UTC}")
    private String defaultTimezone;

    @Value("${app.user.timezone.enabled:true}")
    private boolean userTimezoneEnabled;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Get the current time in the system's default timezone
     */
    public ZonedDateTime now() {
        return ZonedDateTime.now(getSystemZoneId());
    }

    /**
     * Get the current time in a specific timezone
     */
    public ZonedDateTime now(String timezone) {
        if (!isValidTimezone(timezone)) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone);
        }
        return ZonedDateTime.now(ZoneId.of(timezone));
    }

    /**
     * Convert a LocalDateTime to ZonedDateTime using system timezone
     */
    public ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(getSystemZoneId());
    }

    /**
     * Convert a LocalDateTime to ZonedDateTime using specific timezone
     */
    public ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, String timezone) {
        return localDateTime.atZone(ZoneId.of(timezone));
    }

    /**
     * Convert ZonedDateTime from one timezone to another
     */
    public ZonedDateTime convertTimezone(ZonedDateTime dateTime, String targetTimezone) {
        return dateTime.withZoneSameInstant(ZoneId.of(targetTimezone));
    }

    /**
     * Convert to user's timezone (if enabled)
     */
    public ZonedDateTime toUserTimezone(ZonedDateTime dateTime, String userTimezone) {
        if (userTimezoneEnabled && userTimezone != null) {
            return convertTimezone(dateTime, userTimezone);
        }
        return dateTime;
    }

    /**
     * Parse ISO date string to ZonedDateTime
     */
    public ZonedDateTime parseISO(String isoDateTime) {
        return ZonedDateTime.parse(isoDateTime, ISO_FORMATTER);
    }

    /**
     * Format ZonedDateTime to ISO string
     */
    public String formatISO(ZonedDateTime dateTime) {
        return ISO_FORMATTER.format(dateTime);
    }

    /**
     * Get system default ZoneId
     */
    public ZoneId getSystemZoneId() {
        return ZoneId.of(defaultTimezone);
    }

    /**
     * Get system default timezone string
     */
    public String getSystemTimezone() {
        return defaultTimezone;
    }

    /**
     * Validate if a timezone string is valid
     */
    public boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * Get offset for a timezone at current time
     */
    public String getTimezoneOffset(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZoneOffset offset = zoneId.getRules().getOffset(Instant.now());
        return offset.toString();
    }
}