package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.BaseStepHandler;
import org.springframework.stereotype.Component;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@TaskComponent(name = "OpenAIChatStepHandler")
@Component
public class OpenAIChatStepHandler extends BaseStepHandler {

    @TaskService(name = "OpenAIChatStepHandler")
    @Override
    public void execute(AppContext context) {
        System.out.println("OpenAiChatStepHandler.handle");
    }
}
