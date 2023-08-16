package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志时间类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021/8/19
 */
@Getter
public enum LogTimeTypeEnum implements IEnumable<Integer> {

    /**
     * 今天 00:00:00 ~ 现在
     */
    TODAY(1, "今天", "Today") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.HOURS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_HOUR_FORMAT;
        }
    },

    /**
     * 过去7天，七天前的现在 ~ 现在
     */
    LAST_7D(2, "过去7天", "Last 7 days") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusDays(7);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_FORMAT;
        }
    },

    /**
     * 过去4周，四周前的现在 ~ 现在
     */
    LAST_4W(3, "过去4周", "Last 4 weeks") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusWeeks(4);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_FORMAT;
        }
    },

    /**
     * 过去3月，三月前的现在 ~ 现在
     */
    LAST_3M(4, "过去3月", "Last 3 months") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusMonths(3);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_FORMAT;
        }
    },

    /**
     * 过去12月，十二月前的现在 ~ 现在
     */
    LAST_12M(5, "过去12月", "Last 12 months") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusMonths(12);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.MONTHS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return MONTH_FORMAT;
        }
    },

    /**
     * 本月至今，本月第一天的00:00:00 ~ 现在
     */
    LAST_M_T(6, "本月至今", "This month to now") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_FORMAT;
        }
    },

    /**
     * 本季度至今，本季度第一天的00:00:00 ~ 现在
     */
    LAST_Q_T(7, "本季度至今", "This quarter to now") {
        @Override
        public LocalDateTime getStartTime() {
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 获取当前月份的值
            int currentMonthValue = now.getMonthValue();
            // 计算本季度的开始月份
            int startMonthValue = ((currentMonthValue - 1) / 3) * 3 + 1;
            // 本季度的开始时间
            return now.withMonth(startMonthValue).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return DATE_FORMAT;
        }
    },

    /**
     * 本年至今，本年第一天的00:00:00 ~ 现在
     */
    LAST_Y_T(8, "本年至今", "This year to now") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            // 年初到现在时间超过 3 个月。按照月分组，否则按照天分组
            return LocalDateTime.now().minusMonths(3).isAfter(getStartTime()) ? ChronoUnit.MONTHS : ChronoUnit.DAYS;
        }

        @Override
        public String getFormatByGroupUnit() {
            return getGroupUnit().equals(ChronoUnit.MONTHS) ? MONTH_FORMAT : DATE_FORMAT;
        }
    },

    /**
     * 所有时间，null ~ null
     */
    ALL(9, "所有时间", "All") {
        @Override
        public LocalDateTime getStartTime() {
            return LogTimeTypeEnum.LAST_3M.getStartTime();
        }

        @Override
        public LocalDateTime getEndTime() {
            return LogTimeTypeEnum.LAST_3M.getEndTime();
        }

        @Override
        public ChronoUnit getGroupUnit() {
            return LogTimeTypeEnum.LAST_3M.getGroupUnit();
        }

        @Override
        public String getFormatByGroupUnit() {
            return LogTimeTypeEnum.LAST_3M.getFormatByGroupUnit();
        }

    };

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 英文标签
     */
    private final String labelEn;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String DATE_HOUR_FORMAT = "yyyy-MM-dd HH";

    private static final String MONTH_FORMAT = "yyyy-MM";

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    public abstract LocalDateTime getStartTime();

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public abstract LocalDateTime getEndTime();

    /**
     * 获取分组时间单位
     *
     * @return 分组时间单位
     */
    public abstract ChronoUnit getGroupUnit();

    /**
     * 获取分组的时间格式
     *
     * @return 时间格式
     */
    public abstract String getFormatByGroupUnit();

    /**
     * 构造函数
     *
     * @param code  编码
     * @param label 标签
     */
    LogTimeTypeEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 根据类型获取分组时间单位
     *
     * @param timeType 类型
     * @return 分组时间单位
     */
    public static List<LocalDateTime> dateTimeRange(LogTimeTypeEnum timeType) {
        return dateTimeRange(timeType.getStartTime(), timeType.getEndTime(), timeType.getGroupUnit());
    }

    /**
     * 获取时间范围内的所有时间
     *
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @param unit          时间单位
     * @return 时间范围内的所有时间
     */
    public static List<LocalDateTime> dateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime, ChronoUnit unit) {
        List<LocalDateTime> dateTimeRange = new ArrayList<>();
        LocalDateTime currentDateTime = startDateTime;

        while (!currentDateTime.isAfter(endDateTime)) {
            dateTimeRange.add(currentDateTime);
            currentDateTime = currentDateTime.plus(1, unit);
        }

        return dateTimeRange;
    }

    /**
     * 获取所有的选项
     *
     * @return 所有的选项
     */
    public static List<Option> getOptions() {
        return Arrays.stream(values())
                .map(item -> Option.of(item.getCode(), item.getLabel(), item.getLabelEn()))
                .collect(Collectors.toList());
    }
}
