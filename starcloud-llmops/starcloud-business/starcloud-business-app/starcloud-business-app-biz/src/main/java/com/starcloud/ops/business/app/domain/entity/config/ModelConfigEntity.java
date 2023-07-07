package com.starcloud.ops.business.app.domain.entity.config;


import lombok.Data;

@Data
public class ModelConfigEntity {

    /**
     * 提供者  ： openai
     */
    private String provider;

    /**
     * 模型  ：gpt-3.5-turbo
     */
    private String modelName;

    /**
     * 模型配置
     */
    private OpenaiCompletionParams completionParams;


}
