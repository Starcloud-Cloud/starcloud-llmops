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
    DOCUMENT,

    /**
     *  HTML
     */
    HTML,

    /**
     * 字符
     */
    CHARACTERS,

    ;


}
