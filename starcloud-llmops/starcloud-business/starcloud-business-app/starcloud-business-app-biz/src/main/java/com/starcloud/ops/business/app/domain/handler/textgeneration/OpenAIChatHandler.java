package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Open AI 执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class OpenAIChatHandler extends BaseHandler<OpenAIChatHandler.Request, String> {

    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private StreamingSseCallBackHandler streamingSseCallBackHandler;

    public OpenAIChatHandler(StreamingSseCallBackHandler streamingSseCallBackHandler) {
        this.streamingSseCallBackHandler = streamingSseCallBackHandler;
    }

    @Override
    protected HandlerResponse<String> _execute(HandlerContext<OpenAIChatHandler.Request> context) {

        Request request = context.getRequest();
        String prompt = request.getPrompt();
        //prompt = "hi, what you name?";

        HandlerResponse appStepResponse = new HandlerResponse();
        appStepResponse.setSuccess(false);
        appStepResponse.setStepConfig(JSONUtil.toJsonStr(request));
        //appStepResponse.setStepConfig(JSON.toJSONString(variablesMaps));
        appStepResponse.setMessage(prompt);
        appStepResponse.setMessageUnitPrice(BigDecimal.valueOf(0.0200));
        appStepResponse.setAnswerUnitPrice(BigDecimal.valueOf(0.0200));

        try {

            ChatOpenAI chatOpenAI = new ChatOpenAI();
            chatOpenAI.setStream(true);
            chatOpenAI.setMaxTokens(request.getMaxTokens());
            chatOpenAI.setTemperature(request.getTemperature());
            //chatOpenAI.setVerbose(true);
//            chatOpenAI.addCallbackHandler(new StreamingSseCallBackHandler(context.getSseEmitter()));

            chatOpenAI.addCallbackHandler(this.getStreamingSseCallBackHandler());
            List<List<BaseMessage>> chatMessages = Arrays.asList(
                    Arrays.asList(new HumanMessage(prompt))
            );

            //appStepResponse.setStepConfig(chatOpenAI);

            ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(chatMessages);

            BaseLLMUsage baseLLMUsage = chatResult.getUsage();
            String msg = chatResult.getText();

            appStepResponse.setAnswer(msg);
            appStepResponse.setSuccess(true);

            appStepResponse.setOutput(msg);

            appStepResponse.setMessageTokens(baseLLMUsage.getPromptTokens());
            appStepResponse.setAnswerTokens(baseLLMUsage.getCompletionTokens());

            BigDecimal messagePrice = BigDecimal.valueOf(appStepResponse.getMessageTokens()).multiply(appStepResponse.getMessageUnitPrice()).divide(BigDecimal.valueOf(1000));
            BigDecimal answerPrice = BigDecimal.valueOf(appStepResponse.getAnswerTokens()).multiply(appStepResponse.getAnswerUnitPrice()).divide(BigDecimal.valueOf(1000));

            appStepResponse.setTotalTokens(baseLLMUsage.getTotalTokens());
            appStepResponse.setTotalPrice(messagePrice.add(answerPrice));


        } catch (OpenAiHttpException exc) {

            appStepResponse.setErrorCode(exc.code);
            appStepResponse.setErrorMsg(exc.getMessage());

            this.getStreamingSseCallBackHandler().completeWithError(exc);

        } catch (Exception exc) {

            appStepResponse.setErrorCode("001");
            appStepResponse.setErrorMsg(exc.getMessage());

            this.getStreamingSseCallBackHandler().completeWithError(exc);
        }


        return appStepResponse;
    }


    @Data
    public static class Request {

        /**
         * 后续新参数 都是一个个独立字段即可
         */
        private String prompt;

        private Double temperature = 0.7d;

        private Double topP = 1d;

        private Integer n = 1;

        private Boolean stream = false;

        private List<String> stop;

        private Integer maxTokens = 500;

        private Double presencePenalty = 0d;

        private Double frequencyPenalty = 0d;

        private BaseCallbackHandler llmCallbackHandler;

    }
}
