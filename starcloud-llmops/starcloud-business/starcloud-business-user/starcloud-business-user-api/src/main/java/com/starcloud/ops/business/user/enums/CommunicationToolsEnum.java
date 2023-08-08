package com.starcloud.ops.business.user.enums;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;

import java.util.Arrays;

public enum CommunicationToolsEnum implements IntArrayValuable {

    EMAIL(1, "EMAIL"),

    MOBILE_PHONE(2, "MOBILE_PHONE");

    private int code;

    private String name;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CommunicationToolsEnum::getCode).toArray();



    CommunicationToolsEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
