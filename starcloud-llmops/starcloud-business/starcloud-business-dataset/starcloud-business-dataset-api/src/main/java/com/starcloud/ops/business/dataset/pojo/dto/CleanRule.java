package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CleanRule {

    /**
     * 删除连续空格
     */
    private Boolean removeConsecutiveSpaces;

    /**
     * 删除连续换行符
     */
    private Boolean removeConsecutiveNewlines;
    /**
     * 删除制表符
     */
    private Boolean removeConsecutiveTabs;

    /**
     * 删除所有的 URL 和电子邮件地址
     */
    private Boolean removeUrlsEmails;

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
