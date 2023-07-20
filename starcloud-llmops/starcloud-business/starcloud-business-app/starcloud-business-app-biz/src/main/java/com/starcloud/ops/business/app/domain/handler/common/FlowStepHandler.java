package com.starcloud.ops.business.app.domain.handler.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.context.AppContext;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public abstract class FlowStepHandler extends BaseActionHandler {

    /**
     * 返回 action 对对应的入参类型，按 jsonschemas 处理
     * 最后统一所有 action的 参数
     *
     * @param context
     * @return
     */
    public abstract Class<?> getInputCls(AppContext context);


    /**
     * 返回 action 对对应的入参类型，按 jsonschemas 处理
     * 最后统一所有 action的 参数
     *
     * @param context
     * @return
     */
    public abstract JsonNode getInputSchemas(AppContext context);

    @Override
    public Boolean isFlowStepHandler() {
        return true;
    }

    @Override
    public Boolean isFunctionHandler() {
        return false;
    }
}
