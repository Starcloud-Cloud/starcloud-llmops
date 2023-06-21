package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.StreamingStdOutCallbackHandler;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "OpenAIChatActionHandler")
public class OpenAIChatActionHandler extends FlowStepHandler {


    @NoticeSta
    @TaskService(name = "OpenAIChatActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        WorkflowStepWrapper appStepWrapper = context.getCurrentAppStepWrapper();

        String prompt = appStepWrapper.getContextVariablesValue("prompt", "hi, what you name?");

        Map<String, Object> variablesMaps = appStepWrapper.getContextVariablesMaps();

        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(false);
        appStepResponse.setStepVariables(JSON.toJSONString(variablesMaps));
        appStepResponse.setStepConfig(JSON.toJSONString(variablesMaps));
        appStepResponse.setMessage(prompt);

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setStream(true);
        //chatOpenAI.setVerbose(true);
        chatOpenAI.addCallbackHandler(new StreamingStdOutCallbackHandler(context.getHttpServletResponse()));

        ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(Arrays.asList(
                Arrays.asList(HumanMessage.builder().content(prompt).build())
        ));


        if (chatResult == null) {
            appStepResponse.setSuccess(false);
            return appStepResponse;
        }

        BaseLLMUsage baseLLMUsage = chatResult.getUsage();
        String msg = chatResult.getText();

        appStepResponse.setAnswer(msg);
        appStepResponse.setSuccess(true);

        appStepResponse.setMessageTokens(baseLLMUsage.getPromptTokens());
        appStepResponse.setMessageUnitPrice(BigDecimal.valueOf(0.0200));
        appStepResponse.setAnswerTokens(baseLLMUsage.getCompletionTokens());
        appStepResponse.setAnswerUnitPrice(BigDecimal.valueOf(0.0200));

        BigDecimal messagePrice = BigDecimal.valueOf(appStepResponse.getMessageTokens()).multiply(appStepResponse.getMessageUnitPrice()).divide(BigDecimal.valueOf(1000));
        BigDecimal answerPrice = BigDecimal.valueOf(appStepResponse.getAnswerTokens()).multiply(appStepResponse.getAnswerUnitPrice()).divide(BigDecimal.valueOf(1000));

        appStepResponse.setTotalTokens(baseLLMUsage.getTotalTokens());
        appStepResponse.setTotalPrice(messagePrice.add(answerPrice));


        return appStepResponse;
    }
}
