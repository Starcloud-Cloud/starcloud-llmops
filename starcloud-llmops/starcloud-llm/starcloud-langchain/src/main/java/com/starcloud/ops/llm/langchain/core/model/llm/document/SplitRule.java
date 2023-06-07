package com.starcloud.ops.llm.langchain.core.model.llm.document;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SplitRule {

    private Boolean automatic;

    private Boolean removeExtraSpaces;

    private Boolean removeUrlsEmails;

    private Integer maxTokens;

    private String separator;

    private String pattern;

}
