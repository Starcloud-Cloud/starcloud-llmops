package com.starcloud.ops.business.app.domain.entity.config;

import lombok.Data;

@Data
public class OpenaiCompletionParams {

    /**
     * 模型  ：gpt-3.5-turbo
     */
    private String model = "gpt-3.5-turbo";

    private Integer maxTokens = 1000;

    private Double temperature = 1d;

    private Double topP;

    private Double presencePenalty;

    private Double frequencyPenalty;

    private Integer n = 1;

    private Boolean stream = false;


}
