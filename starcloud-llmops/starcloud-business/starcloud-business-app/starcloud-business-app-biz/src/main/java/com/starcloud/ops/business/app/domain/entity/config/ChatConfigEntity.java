package com.starcloud.ops.business.app.domain.entity.config;

import com.starcloud.ops.business.app.domain.entity.action.LLMFunctionEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ChatConfigEntity extends BaseConfigEntity {

    private String code;

    private String prePrompt;

    private VariableEntity variable;

    private ModelConfigEntity modelConfig;

    private List<DatesetEntity> datesetEntities;

    private SuggestedQuestionEntity suggestedQuestion;

    /**
     * 挂载的 functions 列表
     */
    private List<LLMFunctionEntity> functions;

    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    @Override
    public void validate() {

    }
}
