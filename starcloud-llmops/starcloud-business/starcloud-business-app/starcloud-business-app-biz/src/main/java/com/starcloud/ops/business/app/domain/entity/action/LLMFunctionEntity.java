package com.starcloud.ops.business.app.domain.entity.action;

import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import lombok.Data;

/**
 * action 函数实体类
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

}
