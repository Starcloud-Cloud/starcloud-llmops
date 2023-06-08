package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum SourceDataCreateEnum {

    BROWSER_INTERFACE("Browser Interface"),
    EXTERNAL_API("External API");
    ;

    /**
     * 存储器
     */
    private final String name;

}