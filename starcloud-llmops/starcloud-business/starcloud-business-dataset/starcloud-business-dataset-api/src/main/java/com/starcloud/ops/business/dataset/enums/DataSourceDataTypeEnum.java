package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据来源类型
 * @author Alan Cusack
 */

@Getter
@AllArgsConstructor
public enum DataSourceDataTypeEnum {

    /**
     * 文档
     */
    DOCUMENT("文档"),

    /**
     *  HTML
     */
    HTML("网页"),

    /**
     * 字符
     */
    CHARACTERS("自定义文本"),

    ;


    private final String name;




}
