package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum FansNumEnum {

    hundred(100,"100起"),
    thousands(1000,"1000起"),
    two_thousands(2000,"2000起"),
    five_thousands(5000,"5000起"),
    ;

    private Integer minNum;

    private String desc;


    FansNumEnum(Integer minNum, String desc) {
        this.minNum = minNum;
        this.desc = desc;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(FansNumEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getMinNum());
                    return option;
                }).collect(Collectors.toList());
    }
}
