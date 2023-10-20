package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.chat.momory.ConversationSummaryDbMessageMemory;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.template.ChatMemoryPromptTemplate;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatQwen;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        return this._executeGpt(context);
    }

    private HandlerResponse<String> _executeGpt(HandlerContext<OpenAIChatHandler.Request> context) {

        Request request = context.getRequest();
        String prompt = request.getPrompt();
        //prompt = "hi, what you name?";

        HandlerResponse appStepResponse = new HandlerResponse();
        appStepResponse.setSuccess(false);
        appStepResponse.setStepConfig(JSONUtil.toJsonStr(request));
        //appStepResponse.setStepConfig(JSON.toJSONString(variablesMaps));
        appStepResponse.setMessage(prompt);

        ModelTypeEnum modelType = TokenCalculator.fromName(request.getModel());


        //零时切换
        if (ModelTypeEnum.GPT_3_5_TURBO.equals(modelType)) {
            modelType = ModelTypeEnum.QWEN;
        }

        appStepResponse.setMessageUnitPrice(TokenCalculator.getUnitPrice(modelType, true));
        appStepResponse.setAnswerUnitPrice(TokenCalculator.getUnitPrice(modelType, false));


        try {

            BaseLLMUsage baseLLMUsage;
            String msg;

            if (ModelTypeEnum.QWEN.equals(modelType)) {

                BaseLLMResult<GenerationResult> result = this._executeQwen(request);

                baseLLMUsage = result.getUsage();
                msg = result.getText();

                log.info("qwen run: {}, {}", baseLLMUsage, msg);

            } else {

                ChatOpenAI chatOpenAI = new ChatOpenAI();

                chatOpenAI.setModel(request.getModel());
                chatOpenAI.setStream(request.getStream());
                chatOpenAI.setMaxTokens(request.getMaxTokens());
                chatOpenAI.setTemperature(request.getTemperature());

                chatOpenAI.addCallbackHandler(this.getStreamingSseCallBackHandler());

                //数据集支持

                List<List<BaseMessage>> chatMessages = Collections.singletonList(
                        Collections.singletonList(new HumanMessage(prompt))
                );

                ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(chatMessages);

                baseLLMUsage = chatResult.getUsage();
                msg = chatResult.getText();
            }

            //组装参数
            appStepResponse.setAnswer(msg);
            appStepResponse.setSuccess(true);

            appStepResponse.setOutput(msg);

            appStepResponse.setMessageTokens(baseLLMUsage.getPromptTokens());
            appStepResponse.setAnswerTokens(baseLLMUsage.getCompletionTokens());

            Long messageTokens = baseLLMUsage.getPromptTokens();
            Long answerTokens = baseLLMUsage.getCompletionTokens();
            BigDecimal totalPrice = TokenCalculator.getTextPrice(messageTokens, modelType, true).add(TokenCalculator.getTextPrice(answerTokens, modelType, false));

            appStepResponse.setTotalTokens(baseLLMUsage.getTotalTokens());
            appStepResponse.setTotalPrice(totalPrice);


        } catch (OpenAiHttpException exc) {

            appStepResponse.setErrorCode(ErrorCodeConstants.OPENAI_ERROR.getCode());
            appStepResponse.setErrorMsg(exc.getMessage());
            log.error("OpenAIChatHandler OpenAi fail: {}", exc.getMessage(), exc);

            throw ServiceExceptionUtil.exception(ErrorCodeConstants.OPENAI_ERROR);
        }


        return appStepResponse;
    }


    @JsonIgnore
    @JSONField(serialize = false)
    private BaseLLMResult<GenerationResult> _executeQwen(Request request) {

        String prompt = request.getPrompt();

        ChatQwen chatQwen = new ChatQwen();

        chatQwen.setTopP(request.getTemperature());
        chatQwen.setStream(false);
        chatQwen.addCallbackHandler(this.getStreamingSseCallBackHandler());

        ChatPromptTemplate chatPromptTemplate = buildChatPromptTemplate(prompt);

        LLMChain<GenerationResult> llmChain = new LLMChain<>(chatQwen, chatPromptTemplate);

        BaseVariable humanInput = BaseVariable.newString("input", prompt);

        BaseLLMResult<GenerationResult> result = llmChain.call(Arrays.asList(humanInput));

        return result;
    }


    @JsonIgnore
    @JSONField(serialize = false)
    public ChatPromptTemplate buildChatPromptTemplate(String prompt) {

        PromptTemplate promptTmp = new PromptTemplate(prompt);
        SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(promptTmp);

        return ChatMemoryPromptTemplate.fromMessages(new ArrayList<>());
    }


    @Data
    public static class Request {

        private String model = ModelTypeEnum.GPT_3_5_TURBO.getName();

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

        /**
         * 数据集支持
         */
        private List<String> docsUid;

//        @Deprecated
//        private BaseCallbackHandler llmCallbackHandler;
//
//        private SseEmitter sseEmitter;

    }
}
