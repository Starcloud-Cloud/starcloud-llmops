
package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 数据来源类型
 *
 * @author Alan Cusack
 */

@Getter
@AllArgsConstructor
public enum DataSourceDataModelEnum {

    /**
     * 问答
     */
    QUESTION_AND_ANSWERS(0, "问答"),

    /**
     * 文档
     */
    DOCUMENT(1, "文档"),

    ;


    private final Integer status;
    private final String name;


}
