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
    DOCUMENT("文档","网页 URL 地址;"),

    /**
     *  HTML
     */
    HTML("网页","支持 PDF、Word 文档、txt文件、markdown 文件、csv 等文件;"),

    /**
     * 字符
     */
    CHARACTERS("文本","自定义文本内容;"),

    ;


    private final String name;

    private final String description;




}
