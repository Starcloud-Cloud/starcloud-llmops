package com.starcloud.ops.business.app.domain.entity.config;

import lombok.Data;

@Data
public class OpenaiCompletionParams {

    private Integer maxTokens;

    private Double temperature;

    private Double topP;

    private Double presencePenalty;

    private Double frequencyPenalty;

    private Integer n = 1;

    private Boolean stream = true;


}
