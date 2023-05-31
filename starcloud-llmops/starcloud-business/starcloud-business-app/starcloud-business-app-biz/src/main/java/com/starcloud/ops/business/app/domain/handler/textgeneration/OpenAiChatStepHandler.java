package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import com.starcloud.ops.business.app.domain.entity.BaseStepEntity;
import com.starcloud.ops.business.app.domain.handler.BaseStepHandler;
import org.springframework.stereotype.Component;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@TaskComponent(name = "OpenAiChatStepHandler")
@Component
public class OpenAiChatStepHandler extends BaseStepHandler {


    @NoticeResult
    @TaskService(name = "OpenAiChatStepHandler")
    @Override
    public void execute() {
        System.out.println("OpenAiChatStepHandler.handle");
    }
}
