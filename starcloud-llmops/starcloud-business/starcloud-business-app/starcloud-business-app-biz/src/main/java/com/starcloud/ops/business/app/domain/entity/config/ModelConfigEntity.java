package com.starcloud.ops.business.app.domain.entity.config;


import lombok.Data;

@Data
public class ModelConfigEntity {

    /**
     * 提供者  ： openai
     */
    private String provider;

    /**
     * 模型配置
     */
    private OpenaiCompletionParams completionParams;


}
