package com.starcloud.ops.business.app.domain.handler.common;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public abstract class FlowStepHandler extends BaseActionHandler {


    @Override
    public Boolean isFlowStepHandler() {
        return true;
    }

    @Override
    public Boolean isFunctionHandler() {
        return false;
    }
}
