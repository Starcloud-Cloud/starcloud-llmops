package com.starcloud.ops.business.app.enums.xhs.poster;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum PosterVariableModelEnum implements IEnumable<Integer> {

    USER(1, "用户填写"),

    VARIABLE(2, "变量替换"),

    AI(3, "AI生成"),

    MULTIMODAL(4, "AI多模态生成");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String label;


    PosterVariableModelEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
