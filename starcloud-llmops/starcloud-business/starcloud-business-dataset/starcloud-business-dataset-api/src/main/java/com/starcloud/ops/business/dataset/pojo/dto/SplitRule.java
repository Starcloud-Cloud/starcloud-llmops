package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Data
@ToString
public class SplitRule {

    /**
     * 默认清洗策略 只去除空格
     */
    private Boolean automatic;

    /**
     * 删除空白区域
     */
    private Boolean removeExtraSpaces;

    /**
     * 删除链接中的邮箱
     */
    private Boolean removeUrlsEmails;

    @Max(3000)
    @Min(100)
    private Integer chunkSize;

    /**
     * 分隔符
     */
    private List<String> separator;

    /**
     * 自定义清洗正则表达式
     * 替换为空字符串
     */
    private String pattern;

}
