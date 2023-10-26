package com.starcloud.ops.business.app.enums.image;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum ImageTaskConfigTypeEnum implements IEnumable<Integer> {

    /**
     * 文字描述
     */
    TEXT_PROMPT(1, "文字描述", "Text Prompt"),

    /**
     * 快捷模板
     */
    TEMPLATE(2, "快捷模版", "Templates"),

    /**
     * 高级自定义
     */
    ADVANCED(3, "高级自定义", "Advanced");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 标签英文
     */
    private final String labelEn;

    ImageTaskConfigTypeEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }
}
