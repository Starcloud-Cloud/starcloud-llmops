package com.starcloud.ops.business.app.domain.handler;

import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.TaskService;
import com.starcloud.ops.business.app.domain.context.AppContext;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public abstract class BaseStepHandler {

    public abstract void execute(AppContext context);
}
