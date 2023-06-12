package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppStepResponse;
import com.starcloud.ops.business.app.domain.handler.BaseStepHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.StreamingStdOutCallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "OpenAIChatStepHandler")
@Component
public class OpenAIChatStepHandler extends BaseStepHandler {

    @NoticeSta
    @TaskService(name = "OpenAIChatStepHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public AppStepResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setStream(true);
        chatOpenAI.setVerbose(true);
        chatOpenAI.addCallbackHandler(new StreamingStdOutCallbackHandler(context.getHttpServletResponse()));

        String msg = chatOpenAI.call(Arrays.asList(HumanMessage.builder().content("hi, what you name?").build()));

        AppStepResponse appStepResponse = new AppStepResponse();
        appStepResponse.setData(msg);
        appStepResponse.setSuccess(true);

        return appStepResponse;
    }
}
