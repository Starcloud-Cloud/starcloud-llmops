package com.starcloud.ops.business.app.enums;

import com.starcloud.ops.business.app.domain.entity.BaseStepEntity;
import com.starcloud.ops.business.app.domain.handler.OpenAiChatStepHandler;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public enum StepTypeEnum {

    /**
     * OpenAiChat 步骤
     */
//    OPEN_AI_CHAT_STEP("1EFFA33C-C099-BE3A-94B9-AFE1534372C5", OpenAiChatStepHandler.class, "OpenAi Chat 步骤"),

    ;

    /**
     * 步骤类型编码
     */
    @Getter
    private final String code;

    /**
     * 步骤实体类
     */
    @Getter
    private final Class<? extends BaseStepEntity> entity;

    /**
     * 步骤类型说明
     */
    @Getter
    private final String message;

    /**
     * 用 Map 将枚举在初始化时候缓存，方便后续查询
     */
    private static final Map<String, StepTypeEnum> STEP_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(StepTypeEnum.values()).forEach(item -> STEP_TYPE_MAP.put(item.name(), item));
    }

    StepTypeEnum(String code, Class<? extends BaseStepEntity> entity, String message) {
        this.code = code;
        this.entity = entity;
        this.message = message;
    }

    /**
     * 根据 name 获取枚举
     *
     * @param name 枚举名称
     * @return 枚举
     */
    public static StepTypeEnum getByName(String name) {
        if (STEP_TYPE_MAP.containsKey(name)) {
            return STEP_TYPE_MAP.get(name);
        }
        throw new IllegalArgumentException("No enum constant " + StepTypeEnum.class.getCanonicalName() + "." + name);
    }

    /**
     * 根据 name 获取枚举的 entity
     *
     * @param name 枚举名称
     * @return 枚举的 entity
     */
    public static Class<? extends BaseStepEntity> getEntityByName(String name) {
        return getByName(name).getEntity();
    }
}
