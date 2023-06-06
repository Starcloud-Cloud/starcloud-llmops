package com.starcloud.ops.business.app.domain.handler;

import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppStepResponse;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public abstract class BaseStepHandler {

    /**
     * 执行步骤
     *
     * @param context 上下文
     */
    public abstract AppStepResponse execute(AppContext context, ScopeDataOperator scopeDataOperator);
}
