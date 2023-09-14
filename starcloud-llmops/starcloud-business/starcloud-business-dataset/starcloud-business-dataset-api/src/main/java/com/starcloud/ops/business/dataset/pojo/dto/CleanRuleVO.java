package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CleanRuleVO {

    /**
     *  html 专用规则
     */
    private HTMLCleanRule htmlCleanRule;

    /**
     * 通用规则
     */
    private CommonCleanRule commonCleanRule;
}
