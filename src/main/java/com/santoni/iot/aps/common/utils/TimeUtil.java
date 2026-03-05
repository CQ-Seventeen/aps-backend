package com.santoni.iot.aps.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter YYYY_MM_DD_HH = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private static final DateTimeFormatter STR_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final Pattern YYYY_MM_DD_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static LocalDateTime getStartOfToday() {
        return LocalDateTime.now().toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getStartOfDaysAfter(LocalDateTime startTime, int days) {
        return startTime.plusDays(days).toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getStartOfDaysAfter(int days) {
        return LocalDateTime.now().plusDays(days).toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getStartOfTomorrow() {
        return LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getStartOf(LocalDateTime time) {
        return time.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getEndOf(LocalDateTime time) {
        return time.toLocalDate().atTime(LocalTime.MAX);
    }

    public static String formatGeneralString(LocalDateTime time) {
        if (null == time) {
            return "";
        }
        return time.format(YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDateTime fromGeneralString(String time) {
        if (StringUtils.isBlank(time)) {
            throw new IllegalArgumentException("Time is not right format");
        }
        return LocalDateTime.parse(time, YYYY_MM_DD_HH_MM_SS);
    }

    public static boolean isToday(String date) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        return LocalDate.parse(date, YYYY_MM_DD).equals(LocalDate.now());
    }

    public static LocalDateTime fromYYYYMMDD(String time) {
        if (StringUtils.isBlank(time)) {
            throw new IllegalArgumentException("Time is not right format");
        }
        return LocalDate.parse(time, YYYY_MM_DD).atStartOfDay();
    }

    public static LocalDate dateFromYYYYMMDD(String time) {
        if (StringUtils.isBlank(time)) {
            throw new IllegalArgumentException("Time is not right format");
        }
        return LocalDate.parse(time, YYYY_MM_DD);
    }

    public static String formatYYYYMMDD(LocalDateTime time) {
        if (null == time) {
            return "";
        }
        return time.format(YYYY_MM_DD);
    }

    public static String formatHour(LocalDateTime time) {
        if (null == time) {
            return "";
        }
        return time.format(YYYY_MM_DD_HH);
    }

    public static boolean isYYYYMMDD(String date) {
        return YYYY_MM_DD_PATTERN.matcher(date).matches();
    }

    public static String getPrevDayStr(String curDay, int days) {
        var time = LocalDateTime.parse(curDay, YYYY_MM_DD);
        return time.minusDays(days).format(YYYY_MM_DD);
    }

    public static double daysLeft(LocalDateTime time) {
        var now = LocalDateTime.now();
        if (time.isBefore(now)) {
            return 0.0;
        }
        return Math.ceil((double) Duration.between(now, time).getSeconds() / (24 * 60 * 6)) / 10;
    }

    public static int daysBetweenNow(String date) {
        var start = LocalDate.parse(date, YYYY_MM_DD);
        var now = LocalDate.now();
        return (int) Math.abs(ChronoUnit.DAYS.between(start, now));
    }

    public static String formatNow() {
        return LocalDateTime.now().format(STR_YYYY_MM_DD_HH_MM_SS);
    }
}
