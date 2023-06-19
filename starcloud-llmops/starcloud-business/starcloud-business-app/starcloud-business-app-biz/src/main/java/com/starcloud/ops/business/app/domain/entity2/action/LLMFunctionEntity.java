package com.starcloud.ops.business.app.domain.entity2.action;

import com.starcloud.ops.business.app.domain.entity2.variable.VariableEntity;
import lombok.Data;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class LLMFunctionEntity extends ActionEntity {


    /**
     * 模版变量
     */
    private VariableEntity variable;


    /**
     * 步骤类型
     */
    private String type;

    /**
     * 步骤处理器
     */
    private String handler;


}
