package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据来源类型
 * @author Alan Cusack
 */

@Getter
@AllArgsConstructor
public enum DataSourceTypeEnum {

    /**
     * 微信 - 不需要 分段规则
     */
    WECHAT,

    COMMON,
    ;


}
