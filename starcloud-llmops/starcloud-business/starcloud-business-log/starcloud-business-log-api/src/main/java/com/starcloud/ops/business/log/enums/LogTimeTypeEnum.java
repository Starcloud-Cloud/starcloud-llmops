package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * 日志时间类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021/8/19
 */
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
    },

    /**
     * 过去4周，四周前的现在 ~ 现在
     */
    LAST_4W(2, "过去4周", "Last 4 weeks") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusWeeks(4);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }
    },

    /**
     * 过去3月，三月前的现在 ~ 现在
     */
    LAST_3M(3, "过去3月", "Last 3 months") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusMonths(3);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }
    },

    /**
     * 过去12月，十二月前的现在 ~ 现在
     */
    LAST_12M(4, "过去12月", "Last 12 months") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusMonths(12);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }
    },

    /**
     * 本月至今，本月第一天的00:00:00 ~ 现在
     */
    LAST_M_T(5, "本月至今", "This month to now") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }
    },

    /**
     * 本季度至今，本季度第一天的00:00:00 ~ 现在
     */
    LAST_Q_T(6, "本季度至今", "This quarter to now") {
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
    },

    /**
     * 本年至今，本年第一天的00:00:00 ~ 现在
     */
    LAST_Y_T(7, "本年至今", "This year to now") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
        }

        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.now();
        }
    },

    /**
     * 所有时间，null ~ null
     */
    ALL(8, "所有时间", "All") {
        @Override
        public LocalDateTime getStartTime() {
            return null;
        }

        @Override
        public LocalDateTime getEndTime() {
            return null;
        }
    };

    /**
     *
     */
    @Getter
    private final Integer code;

    /**
     *
     */
    @Getter
    private final String label;

    /**
     * 英文标签
     */
    @Getter
    private final String labelEn;

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
     * 根据类型获取开始时间
     *
     * @param type 类型
     * @return 开始时间
     */
    public static LocalDateTime getStartTimeByType(String type) {
        return getTimeByType(type, true);
    }

    /**
     * 根据类型获取结束时间
     *
     * @param type 类型
     * @return 结束时间
     */
    public static LocalDateTime getEndTimeByType(String type) {
        return getTimeByType(type, false);
    }

    /**
     * 根据类型获取开始时间
     *
     * @param type    类型
     * @param isStart 是否开始时间: true-开始时间, false-结束时间
     * @return 开始时间
     */
    public static LocalDateTime getTimeByType(String type, boolean isStart) {
        for (LogTimeTypeEnum value : LogTimeTypeEnum.values()) {
            if (value.name().equals(type)) {
                if (isStart) {
                    return value.getStartTime();
                } else {
                    return value.getEndTime();
                }
            }
        }
        return null;
    }
}
