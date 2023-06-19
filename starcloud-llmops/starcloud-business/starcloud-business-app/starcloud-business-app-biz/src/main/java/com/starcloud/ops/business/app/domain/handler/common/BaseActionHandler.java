package com.starcloud.ops.business.app.domain.handler.common;

import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity2.action.ActionResponse;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public abstract class BaseActionHandler {

    /**
     * 执行步骤
     *
     * @param context 上下文
     */
    public abstract ActionResponse execute(AppContext context, ScopeDataOperator scopeDataOperator);

    public abstract Boolean isFlowStepHandler();

    public abstract Boolean isFunctionHandler();
}
