package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CleanRuleVO {

    /**
     * 删除连续空格
     */
    private CleanRule URL;

    /**
     * 删除连续换行符
     */
    private CleanRule CHARACTERS;
    /**
     * 删除制表符
     */
    private CleanRule DOCUMENT;

}
