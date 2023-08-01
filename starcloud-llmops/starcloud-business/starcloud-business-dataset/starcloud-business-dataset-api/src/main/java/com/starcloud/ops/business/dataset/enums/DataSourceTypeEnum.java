package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.html.HTML;

/**
 * 数据来源类型
 * @author Alan Cusack
 */

@Getter
@AllArgsConstructor
public enum DataSourceTypeEnum {

    /**
     * 文档
     */
    DOCUMENT,

    /**
     *  URL
     */
    URL,

    /**
     * 字符
     */
    CHARACTERS,

    ;


}
