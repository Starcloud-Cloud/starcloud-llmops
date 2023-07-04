package com.starcloud.ops.framework.common.api.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Objects;

/**
 * Tucc 日期工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2022-06-22
 */
@SuppressWarnings("unused")
@UtilityClass
public class DateUtil {

    /**
     * 将 {@link LocalDate} 转换为 {@link Date}
     *
     * @param localDate {@link LocalDate} 对象
     * @return {@link Date} 对象
     */
    public static Date from(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            throw new IllegalArgumentException("localDate is cannot be null");
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 {@link LocalDateTime} 转换为 {@link Date}
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return {@link Date} 对象
     */
    public static Date from(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            throw new IllegalArgumentException("localDateTime is cannot be null");
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 {@link Date} 转换为 {@link LocalDate}
     *
     * @param date {@link Date} 对象
     * @return {@link LocalDate} 对象
     */
    public static LocalDate toLocalDate(Date date) {
        if (Objects.isNull(date)) {
            throw new IllegalArgumentException("date is cannot be null");
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将字符串格式为 yyyy-MM-dd 的日期转换为 {@link LocalDate}
     *
     * @param date yyyy-MM-dd 格式的日期字符串 <br>
     * @return {@link LocalDate} 对象 <br>
     */
    public static LocalDate toLocalDate(String date) {
        if (StringUtils.isBlank(date)) {
            throw new IllegalArgumentException("date is cannot be blank");
        }

        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    /**
     * 将时间戳转换为 {@link LocalDate}
     *
     * @param timestamp 时间戳 <br>
     *                  如果时间戳为 null，则返回 null
     * @return {@link LocalDate} 对象
     */
    public static LocalDate toLocalDate(Long timestamp) {
        if (Objects.isNull(timestamp)) {
            throw new IllegalArgumentException("timestamp is cannot be null");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 {@link Date} 转换为 {@link LocalDateTime}
     *
     * @param date {@link Date} 对象
     * @return {@link LocalDateTime} 对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (Objects.isNull(date)) {
            throw new IllegalArgumentException("date is cannot be null");
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将字符串格式为 yyyy-MM-dd HH:mm:ss 的日期转换为 {@link LocalDateTime}
     *
     * @param dateTime yyyy-MM-dd HH:mm:ss 格式的日期字符串 <br>
     *                 如果字符串为空，则返回 null
     * @return {@link LocalDateTime} 对象
     */
    public static LocalDateTime toLocalDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            throw new IllegalArgumentException("dateTime is cannot be blank");
        }

        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * 将毫秒时间戳转换为 {@link LocalDateTime}
     *
     * @param timestamp 时间戳 <br>
     *                  如果时间戳为 null，则返回 null
     * @return {@link LocalDateTime} 对象
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        if (Objects.isNull(timestamp)) {
            throw new IllegalArgumentException("timestamp is cannot be null");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 判断 localDate 是否在 start 和 end 之间
     *
     * @param localTime 待判断的时间。如果为 null，则返回 false
     * @param start     开始时间。如果为 null，则返回 false
     * @param end       结束时间。如果为 null，则返回 false
     * @return 如果 localDate 在 startTime 和 endTime 之间，则返回 true，否则返回 false
     */
    public static Boolean isBetween(LocalTime localTime, LocalTime start, LocalTime end) {
        if (Objects.isNull(localTime) || Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }
        return localTime.isAfter(start) && localTime.isBefore(end);
    }

    /**
     * 判断 localDate 是否在 start 和 end 之间
     *
     * @param localDate 待判断的时间。如果为 null，则返回 false
     * @param start     开始时间。如果为 null，则返回 false
     * @param end       结束时间。如果为 null，则返回 false
     * @return 如果 localDate 在 startTime 和 endTime 之间，则返回 true，否则返回 false
     */
    public static Boolean isBetween(LocalDate localDate, LocalDate start, LocalDate end) {
        if (Objects.isNull(localDate) || Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }
        return localDate.isAfter(start) && localDate.isBefore(end);
    }

    /**
     * 判断 localDateTime 是否在 start 和 end 之间
     *
     * @param dateTime 待判断的时间。如果为 null，则返回 false
     * @param start    开始时间。如果为 null，则返回 false
     * @param end      结束时间。如果为 null，则返回 false
     * @return 如果 localDateTime 在 startTime 和 endTime 之间，则返回 true，否则返回 false
     */
    public static Boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(dateTime) || Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }

        return dateTime.isAfter(start) && dateTime.isBefore(end);
    }

    /**
     * 计算指定单位的两个时间的差值
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param unit  单位
     * @return 差值
     */
    public static Long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        if (Objects.isNull(start) || Objects.isNull(end) || Objects.isNull(unit)) {
            throw new IllegalArgumentException("start, end and unit cannot be null");
        }
        return unit.between(start, end);
    }

    /**
     * 获取当前时间的毫秒数
     *
     * @return 毫秒数
     */
    public static Long timestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 获取给定时间的毫秒数, 使用系统默认时区
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return 毫秒数
     */
    public static Long timestamp(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            throw new IllegalArgumentException("localDateTime can't be null");
        }
        return localDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取给定时间和时区的毫秒数
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @param zone          时区
     * @return 毫秒数
     */
    public static Long timestamp(LocalDateTime localDateTime, ZoneOffset zone) {
        if (Objects.isNull(localDateTime) || Objects.isNull(zone)) {
            throw new IllegalArgumentException("localDateTime and zone can't be null");
        }
        return localDateTime.toInstant(zone).toEpochMilli();
    }

    /**
     * 获取当前时间的秒数，使用系统默认时区
     *
     * @return 秒数
     */
    public static Long second() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 获取给定时间的秒数，使用系统默认时区
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return 秒数
     */
    public static Long second(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            throw new IllegalArgumentException("localDateTime can't be null");
        }
        return localDateTime.atZone(ZoneOffset.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取给定时间和时区的秒数
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @param zone          时区
     * @return 秒数
     */
    public static Long second(LocalDateTime localDateTime, ZoneOffset zone) {
        if (Objects.isNull(localDateTime) || Objects.isNull(zone)) {
            throw new IllegalArgumentException("localDateTime and zone can't be null");
        }
        return localDateTime.toEpochSecond(zone);
    }

    /**
     * 获取今天的开始时间
     *
     * @return 今天的开始时间
     */
    public static LocalDateTime todayBegin() {
        return dayBegin(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在天的开始时间
     *
     * @param localDateTime 指定日期
     * @return 指定日期所在天的开始时间
     */
    public static LocalDateTime dayBegin(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            throw new IllegalArgumentException("localDateTime can't be null");
        }
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取今天的结束时间
     *
     * @return 今天的结束时间
     */
    public static LocalDateTime todayEnd() {
        return dayEnd(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在天的结束时间
     *
     * @param localDateTime 指定日期
     * @return 指定日期所在天的结束时间
     */
    public static LocalDateTime dayEnd(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            throw new IllegalArgumentException("localDateTime can't be null");
        }
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取昨天的日期
     *
     * @return 昨天的时间
     */
    public static LocalDate yesterday() {
        return LocalDate.now().minusDays(1);
    }

    /**
     * 获取本周指定周几的日期 <br/>
     * 比如：获取本周周一的日期，调用方法：thisWeekOfDay(DayOfWeek.MONDAY) <br/>
     * 获取本周周日的日期，调用方法：thisWeekOfDay(DayOfWeek.SUNDAY)
     *
     * @param dayOfWeek 指定周几
     * @return 本周指定周几的日期
     */
    public static LocalDate thisWeekOfDay(DayOfWeek dayOfWeek) {
        if (Objects.isNull(dayOfWeek)) {
            throw new IllegalArgumentException("dayOfWeek can't be null");
        }
        return LocalDate.now().with(dayOfWeek);
    }

    /**
     * 根据 dayOfWeek 获取上周的指定周几的日期 <br/>
     * 比如  获取上周三的日期 lastWeekOfDay(DayOfWeek.WEDNESDAY) <br/>
     * 获取上周日的日期 lastWeekOfDay(DayOfWeek.SUNDAY)
     *
     * @param dayOfWeek 指定周几
     * @return 上周的指定周几的日期
     */
    public static LocalDate lastWeekOfDay(DayOfWeek dayOfWeek) {
        if (Objects.isNull(dayOfWeek)) {
            throw new IllegalArgumentException("dayOfWeek can't be null");
        }
        return LocalDate.now().with(TemporalAdjusters.previous(dayOfWeek));
    }

    /**
     * 获取本月的第一天的日期
     *
     * @return 本月的第一天的日期
     */
    public static LocalDate firstDayOfMonth() {
        return firstDayOfMonth(0L);
    }

    /**
     * 获取指定偏移 offset 个月的第一天的日期<br/>
     * offset > 0 时， 表示 offset 个月前的第一天的日期 <br/>
     * offset = 0 时， 表示当月的第一天的日期 <br/>
     * offset < 0 时， 表示 offset 个月后的第一天的日期 <br/>
     *
     * @param offset 月份偏移量。为 null 时，表示当月的第一天的日期
     * @return 指定偏移 offset 个月的第一天的日期
     */
    public static LocalDate firstDayOfMonth(Long offset) {
        return LocalDate.now().minusMonths(Objects.nonNull(offset) ? offset : 0L).with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月的最后一天的日期
     *
     * @return 本月的最后一天的日期
     */
    public static LocalDate lastDayOfMonth() {
        return lastDayOfMonth(0L);
    }

    /**
     * 获取指定偏移 offset 个月的最后一天的日期<br/>
     * offset > 0 时， 表示 offset 个月前的最后一天的日期 <br/>
     * offset = 0 时， 表示当月的最后一天的日期 <br/>
     * offset < 0 时， 表示 offset 个月后的最后一天的日期 <br/>
     *
     * @param offset 月份偏移量。offset 为 null 时，表示当月的最后一天的日期
     * @return 指定偏移 offset 个月的最后一天的日期
     */
    public static LocalDate lastDayOfMonth(Long offset) {
        return LocalDate.now().minusMonths(Objects.nonNull(offset) ? offset : 0L).with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本年的第一天的日期
     *
     * @return 本年的第一天的日期
     */
    public static LocalDate firstDayOfYear() {
        return firstDayOfYear(0L);
    }

    /**
     * 获取指定偏移 offset 个年份的第一天的日期<br/>
     * offset > 0 时， 表示 offset 个年份前的第一天的日期 <br/>
     * offset = 0 时， 表示当年的第一天的日期 <br/>
     * offset < 0 时， 表示 offset 个年份后的第一天的日期 <br/>
     *
     * @param offset 年份偏移量。为 null 时，表示当年的第一天的日期
     * @return 指定偏移 offset 个年份的第一天的日期
     */
    public static LocalDate firstDayOfYear(Long offset) {
        return LocalDate.now().minusYears(Objects.nonNull(offset) ? offset : 0L).with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取本年的最后一天的日期
     *
     * @return 本年的最后一天的日期
     */
    public static LocalDate lastDayOfYear() {
        return lastDayOfYear(0L);
    }

    /**
     * 获取指定偏移 offset 个年份的最后一天的日期<br/>
     * offset > 0 时， 表示 offset 个年份前的最后一天的日期 <br/>
     * offset = 0 时， 表示当年的最后一天的日期 <br/>
     * offset < 0 时， 表示 offset 个年份后的最后一天的日期 <br/>
     *
     * @param offset 年份偏移量，为 null 时，表示当年的最后一天的日期
     * @return 指定偏移 offset 个年份的最后一天的日期
     */
    public static LocalDate lastDayOfYear(Long offset) {
        return LocalDate.now().minusYears(Objects.nonNull(offset) ? offset : 0L).with(TemporalAdjusters.lastDayOfYear());
    }
}
