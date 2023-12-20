package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum SingleMissionStatusEnum implements IEnumable<String> {
    init("init", "待发布"),
    stay_claim("stay_claim", "待认领"),
    claimed("claimed", "已认领"),
    published("published", "用户已发布"),
    pre_settlement("pre_settlement", "预结算"),
    settlement("settlement", "结算"),
    close("close", "关闭"),
    pre_settlement_error("pre_settlement_error", "预结算异常"),
    settlement_error("settlement_error", "结算异常"),
    complete("complete","完成")
    ;

    private final String code;

    private final String desc;

    private static final Map<String, SingleMissionStatusEnum> ENUM_MAP = Arrays.stream(SingleMissionStatusEnum.values())
            .collect(Collectors.toMap(SingleMissionStatusEnum::getCode, Function.identity()));

    SingleMissionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(SingleMissionStatusEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }

    public static SingleMissionStatusEnum valueOfCode(String code) {
        return ENUM_MAP.get(code);
    }
}
