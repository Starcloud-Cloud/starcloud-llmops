package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class HTMLCleanRule {

    /**
     * 白名单列表
     */
    private List<String> whiteList;

    /**
     * 黑名单列表
     */
    private List<String> blackList;

    /**
     * 转换格式 - TXT MarkDown ....
     */
    private String convertFormat;

    /**
     *  设置网页语言 默认为中文 zh
     */
    private String acceptLanguage;
}
