package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CommonCleanRule {

    /**
     * 清除所有的 html 标签
     */
    private Boolean removeAllHtmlTags;

    /**
     * 清除所有的图片
     */
    private Boolean removeAllImage;

    /**
     * 删除连续空格
     */
    private Boolean removeConsecutiveSpaces;

    /**
     * 删除连续换行符
     */
    private Boolean removeConsecutiveNewlines;
    /**
     * 清除制表符
     */
    private Boolean removeConsecutiveTabs;

    /**
     * 清除电子邮件地址
     */
    private Boolean removeUrlsEmails;

    class HtmlRule{
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
}
