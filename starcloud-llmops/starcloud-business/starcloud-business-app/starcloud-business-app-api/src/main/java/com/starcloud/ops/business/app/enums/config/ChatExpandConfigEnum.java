package com.starcloud.ops.business.app.enums.config;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChatExpandConfigEnum implements IntArrayValuable {


    MENU(1, "菜单配置"),

    API(2, "API技能配置"),

    APP_WORKFLOW(3, "应用技能配置"),

    GPT_PLUG(4, "gpt插件技能配置"),

    SYSTEM_HANDLER(5, "系统技能配置");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ChatExpandConfigEnum::getCode).toArray();


    private final Integer code;

    private final String description;



    ChatExpandConfigEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    public static ChatExpandConfigEnum getMenu(Integer code) {
        for (ChatExpandConfigEnum value : ChatExpandConfigEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
