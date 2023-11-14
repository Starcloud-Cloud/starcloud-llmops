package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatQwen;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Open AI 执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@SuppressWarnings("all")
public class OpenAIChatHandler extends BaseHandler<OpenAIChatHandler.Request, String> {

    /**
     * SSE 回调
     */
    private StreamingSseCallBackHandler streamingSseCallBackHandler;

    /**
     * 构造函数
     *
     * @param streamingSseCallBackHandler SSE 回调
     */
    public OpenAIChatHandler(StreamingSseCallBackHandler streamingSseCallBackHandler) {
        this.streamingSseCallBackHandler = streamingSseCallBackHandler;
    }

    /**
     * 执行handler
     *
     * @param context 请求上下文
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @Override
    protected HandlerResponse<String> _execute(HandlerContext<OpenAIChatHandler.Request> context) {
        return this._executeGpt(context);
    }

    /**
     * 执行 GPT handler, 调用 ChatGPT AI模型生成内容
     *
     * @param context 请求上下文
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private HandlerResponse<String> _executeGpt(HandlerContext<OpenAIChatHandler.Request> context) {

        Request request = context.getRequest();
        String prompt = request.getPrompt();

        HandlerResponse<String> appStepResponse = new HandlerResponse<>();
        appStepResponse.setSuccess(false);
        appStepResponse.setStepConfig(JSONUtil.toJsonStr(request));
        appStepResponse.setMessage(prompt);

        ModelTypeEnum modelType = TokenCalculator.fromName(request.getModel());
        appStepResponse.setMessageUnitPrice(TokenCalculator.getUnitPrice(modelType, true));
        appStepResponse.setAnswerUnitPrice(TokenCalculator.getUnitPrice(modelType, false));

        try {
            BaseLLMUsage baseLLMUsage;
            String msg;
            if (ModelTypeEnum.QWEN.equals(modelType)) {
                BaseLLMResult<GenerationResult> result = this._executeQwen(request);
                baseLLMUsage = result.getUsage();
                msg = result.getText();
            } else {

                ChatOpenAI chatOpenAI = new ChatOpenAI();
                chatOpenAI.setModel(request.getModel());
                chatOpenAI.setStream(request.getStream());
                chatOpenAI.setMaxTokens(request.getMaxTokens());
                chatOpenAI.setTemperature(request.getTemperature());
                chatOpenAI.setN(request.getN());
                chatOpenAI.addCallbackHandler(this.getStreamingSseCallBackHandler());

                //数据集支持
                List<List<BaseMessage>> chatMessages = Collections.singletonList(
                        Collections.singletonList(new HumanMessage(prompt))
                );

                ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(chatMessages);

                baseLLMUsage = chatResult.getUsage();
                if (request.getN() == 1) {
                    msg = chatResult.getText();
                } else {
                    List<ChatCompletionResult> collect = CollectionUtil.emptyIfNull(chatResult.getChatGenerations()).stream()
                            .map(ChatGeneration::getGenerationInfo).filter(Objects::nonNull).collect(Collectors.toList());
                    if (CollectionUtil.isEmpty(collect)) {
                        msg = "[]";
                    } else {
                        ChatCompletionResult chatCompletionResult = collect.get(0);
                        List<ChatCompletionChoice> choices = CollectionUtil.emptyIfNull(chatCompletionResult.getChoices()).stream().peek(item -> item.setFinishReason(null)).collect(Collectors.toList());
                        msg = JSONUtil.toJsonStr(choices);
                    }
                }
            }

            //组装参数
            appStepResponse.setSuccess(true);
            appStepResponse.setAnswer(msg);
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


    /**
     * 执行 通义千问 handler, 调用 通义千问 AI模型生成内容
     *
     * @param request 请求
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private BaseLLMResult<GenerationResult> _executeQwen(Request request) {
        log.info("通义千问执行开始: {}", JSONUtil.toJsonStr(request));
        String prompt = request.getPrompt();

        ChatQwen chatQwen = new ChatQwen();
        chatQwen.setTopP(request.getTemperature());
        chatQwen.setStream(false);
        chatQwen.addCallbackHandler(this.getStreamingSseCallBackHandler());

        LLMChain<GenerationResult> llmChain = new LLMChain<>(chatQwen, buildChatPromptTemplate(prompt));
        llmChain.setMemory(null);

        BaseVariable humanInput = BaseVariable.newString("input", "");
        BaseLLMResult<GenerationResult> result = llmChain.call(Collections.singletonList(humanInput));

        log.info("通义千问执行结果: {}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 构建聊天模板
     *
     * @param prompt 提示
     * @return 聊天模板
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public ChatPromptTemplate buildChatPromptTemplate(String prompt) {
        StringPromptTemplate stringPromptTemplate = new PromptTemplate(prompt, new ArrayList<>());
        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(stringPromptTemplate);
        return ChatPromptTemplate.fromMessages(Collections.singletonList(humanMessagePromptTemplate));
    }

    /**
     * 请求实体
     */
    @Data
    public static class Request {

        /**
         * AI 模型, 默认 ChatGPT 3.5 Turbo
         */
        private String model = ModelTypeEnum.GPT_3_5_TURBO.getName();

        /**
         * 后续新参数 都是一个个独立字段即可
         */
        private String prompt;

        /**
         * 温度
         */
        private Double temperature = 0.7d;

        /**
         * topP
         */
        private Double topP = 1d;

        /**
         * 生成几条内容
         */
        private Integer n = 1;

        /**
         * 是否流式
         */
        private Boolean stream = false;

        /**
         * 是否使用stop
         */
        private List<String> stop;

        /**
         * 最大生成长度
         */
        private Integer maxTokens = 500;

        /**
         * 预报费
         */
        private Double presencePenalty = 0d;

        /**
         * 频数
         */
        private Double frequencyPenalty = 0d;

        /**
         * 数据集支持
         */
        private List<String> docsUid;

    }
}
