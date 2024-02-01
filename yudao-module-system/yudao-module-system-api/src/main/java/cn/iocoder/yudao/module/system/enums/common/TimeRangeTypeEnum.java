package cn.iocoder.yudao.module.system.enums.common;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * 时间范围类型的枚举
 *
 * @author owen
 */
@AllArgsConstructor
@Getter
public enum TimeRangeTypeEnum implements IntArrayValuable {

    /**
     * 天
     */
    DAY(1),
    /**
     * 周
     */
    WEEK(7),
    /**
     * 月
     */
    MONTH(30),
    /**
     * 年
     */
    YEAR(365),
    ;
    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(TimeRangeTypeEnum::getType).toArray();

    /**
     * 类型
     */
    private final Integer type;

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static TimeRangeTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(TimeRangeTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

    public static LocalDateTime getPlusTimeByRange(Integer type, Integer driftUnit, LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            dateTime = LocalDateTime.now();
        }
        TimeRangeTypeEnum timeRange = getByType(type);
        switch (timeRange) {
            case DAY:
                return dateTime.plusDays(driftUnit);
            case WEEK:
                return dateTime.plusWeeks(driftUnit);
            case MONTH:
                return dateTime.plusMonths(driftUnit);
            case YEAR:
                return dateTime.plusYears(driftUnit);
            default:
                throw new RuntimeException("时间范围未识别");
        }
    }
    public static LocalDateTime getMinusTimeByRange(Integer type, Integer driftUnit, LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            dateTime = LocalDateTime.now();
        }
        TimeRangeTypeEnum timeRange = getByType(type);
        switch (timeRange) {
            case DAY:
                return dateTime.minusDays(driftUnit);
            case WEEK:
                return dateTime.minusWeeks(driftUnit);
            case MONTH:
                return dateTime.minusMonths(driftUnit);
            case YEAR:
                return dateTime.minusYears(driftUnit);
            default:
                throw new RuntimeException("时间范围未识别");
        }
    }



}