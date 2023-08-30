package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据来源类型
 * @author Alan Cusack
 */

@Getter
@AllArgsConstructor
public enum DataSourceDataFormatEnum {

    /**
     * TXT
     */
    TXT("普通文本",".txt"),

    /**
     *  MARKDOWN
     */
    MARKDOWN("MarkDown",".md"),
    ;

    private final String name;
    private final String suffix;


}
