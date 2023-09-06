package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class BlackCleanVO {

    /**
     * 黑名单标签
     */
    private List<String> tags;

    /**
     * 黑名单属性
     */
    private List<Map<String, String>> Attributes;

}
