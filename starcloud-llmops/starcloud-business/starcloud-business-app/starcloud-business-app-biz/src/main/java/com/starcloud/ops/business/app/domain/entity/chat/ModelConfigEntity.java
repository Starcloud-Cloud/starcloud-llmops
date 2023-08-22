package com.starcloud.ops.business.app.domain.entity.chat;


import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import lombok.Data;

@Data
public class ModelConfigEntity {

    /**
     * 提供者  ： openai
     */
    private String provider;

    /**
     * 最大多少tokens对 prompt进行强制总结
     */
    private Integer maxSummaryTokens;

    /**
     * 模型配置
     */
    private OpenaiCompletionParams completionParams;


}
