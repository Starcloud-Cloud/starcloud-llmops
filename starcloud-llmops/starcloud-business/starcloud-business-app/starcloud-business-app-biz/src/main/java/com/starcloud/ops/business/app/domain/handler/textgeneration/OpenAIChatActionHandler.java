package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import io.swagger.v3.oas.models.media.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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


    @Autowired
    private UserBenefitsService userBenefitsService;


    @NoticeSta
    @TaskService(name = "OpenAIChatActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        WorkflowStepWrapper appStepWrapper = context.getCurrentStepWrapper();
        String prompt = context.getContextVariablesValue("prompt", "hi, what you name?");
        //prompt = "hi, what you name?";

        Map<String, Object> variablesMaps = appStepWrapper.getContextVariablesValues(null);

        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(false);
        appStepResponse.setStepVariables(JSON.toJSONString(variablesMaps));
        //appStepResponse.setStepConfig(JSON.toJSONString(variablesMaps));
        appStepResponse.setMessage(prompt);
        appStepResponse.setMessageUnitPrice(BigDecimal.valueOf(0.0200));
        appStepResponse.setAnswerUnitPrice(BigDecimal.valueOf(0.0200));

        try {

            ChatOpenAI chatOpenAI = new ChatOpenAI();
            chatOpenAI.setStream(true);
            chatOpenAI.setMaxTokens(Integer.valueOf(context.getContextVariablesValue("max_tokens", "1000")));
            chatOpenAI.setTemperature(Double.valueOf(context.getContextVariablesValue("temperature", "0.7")));
            //chatOpenAI.setVerbose(true);
            chatOpenAI.addCallbackHandler(new StreamingSseCallBackHandler(context.getSseEmitter()));

            List<List<BaseMessage>> chatMessages = Arrays.asList(
                    Arrays.asList(new HumanMessage(prompt))
            );

            appStepResponse.setStepConfig(chatOpenAI);

            ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(chatMessages);

            BaseLLMUsage baseLLMUsage = chatResult.getUsage();
            String msg = chatResult.getText();

            appStepResponse.setAnswer(msg);
            appStepResponse.setSuccess(true);

            appStepResponse.setMessageTokens(baseLLMUsage.getPromptTokens());
            appStepResponse.setAnswerTokens(baseLLMUsage.getCompletionTokens());

            BigDecimal messagePrice = BigDecimal.valueOf(appStepResponse.getMessageTokens()).multiply(appStepResponse.getMessageUnitPrice()).divide(BigDecimal.valueOf(1000));
            BigDecimal answerPrice = BigDecimal.valueOf(appStepResponse.getAnswerTokens()).multiply(appStepResponse.getAnswerUnitPrice()).divide(BigDecimal.valueOf(1000));

            appStepResponse.setTotalTokens(baseLLMUsage.getTotalTokens());
            appStepResponse.setTotalPrice(messagePrice.add(answerPrice));


            //权益记录
            userBenefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), appStepResponse.getTotalTokens(), Long.valueOf(context.getUser()), context.getConversationId());


        } catch (OpenAiHttpException exc) {

            appStepResponse.setErrorCode(exc.code);
            appStepResponse.setErrorMsg(exc.getMessage());
            context.getSseEmitter().completeWithError(exc);
        } catch (Exception exc) {

            appStepResponse.setErrorCode("001");
            appStepResponse.setErrorMsg(exc.getMessage());
            context.getSseEmitter().completeWithError(exc);
        }


        return appStepResponse;
    }

    @Override
    public Class<?> getInputCls(AppContext context) {
        return null;
    }

    @Override
    public JsonNode getInputSchemas(AppContext context) {

        Map<String, Object> stepParams = context.getContextVariablesValues();

        // 根据步骤的参数 生成 jsonSchemas

        return null;
    }

}
