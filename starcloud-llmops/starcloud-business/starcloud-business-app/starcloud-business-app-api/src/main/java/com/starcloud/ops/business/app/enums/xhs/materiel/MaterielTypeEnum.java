package com.starcloud.ops.business.app.enums.xhs.materiel;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum MaterielTypeEnum implements IEnumable<Integer> {

    /**
     * 书单
     */
    BOOK_LIST(1, "书单"),

    ;

    /**
     * 媒体库CODE
     */
    private final Integer code;

    /**
     * 媒体库名称
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  媒体库CODE
     * @param label 媒体库名称
     */
    MaterielTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
