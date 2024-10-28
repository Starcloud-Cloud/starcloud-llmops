package com.starcloud.ops.business.job.biz.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TriggerTypeEnum  implements IEnumable<Integer> {
//    API(1, "API"),
    CRON(2, "定时执行"),
//    FIXED_RATE(3, "固定频率/毫秒"),
//    FIXED_DELAY(4, "固定延迟/毫秒"),
//    WORKFLOW(5, "工作流"),
//    DAILY_TIME_INTERVAL(11, "每日固定间隔")


    ;

    private final int code;

    private final String desc;

    TriggerTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(TriggerTypeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }

    public static TriggerTypeEnum of(Integer code) {
        for (TriggerTypeEnum value : TriggerTypeEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String getLabel() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
