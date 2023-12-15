package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum AddressEnum implements IEnumable<String> {
    unlimited("unlimited","不限"),
    hangzhou("hangzhou","杭州");

    private String code;

    private String desc;

    AddressEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(AddressEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
