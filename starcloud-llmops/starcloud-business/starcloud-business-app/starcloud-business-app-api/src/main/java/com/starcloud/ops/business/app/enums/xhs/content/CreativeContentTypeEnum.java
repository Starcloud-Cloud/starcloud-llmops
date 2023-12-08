package com.starcloud.ops.business.app.enums.xhs.content;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 状态枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2020-11-03 10:54
 */
@Getter
public enum CreativeContentTypeEnum implements IEnumable<String> {

    /**
     * 图片
     */
    PICTURE("picture", "图片"),

    /**
     * 文字
     */
    COPY_WRITING("copy_writing", "文字模板");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型标签
     */
    private final String label;

    /**
     * 构造器
     *
     * @param code  类型编码
     * @param label 类型标签
     */
    CreativeContentTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 是否包含
     *
     * @param code 类型编码
     * @return 是否包含
     */
    public static boolean contain(String code) {
        for (CreativeContentTypeEnum value : CreativeContentTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
