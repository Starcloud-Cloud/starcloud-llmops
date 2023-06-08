package com.starcloud.ops.llm.langchain.core.model.llm.document;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SplitRule {

    /**
     * 默认清洗策略 只去除空格
     */
    private Boolean automatic;

    private Boolean removeExtraSpaces;

    private Boolean removeUrlsEmails;

    private Integer chunkSize;

    private List<String> separator;

    /**
     * 自定义清洗正则表达式
     * 替换为空字符串
     */
    private String pattern;

}
