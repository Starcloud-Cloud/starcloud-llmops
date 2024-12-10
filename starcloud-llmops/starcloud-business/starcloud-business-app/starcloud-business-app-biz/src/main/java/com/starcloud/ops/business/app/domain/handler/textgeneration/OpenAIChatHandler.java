package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Status;
import com.alibaba.dashscope.exception.ApiException;
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
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
     * 执行大模型，生成内容
     *
     * @param context 请求上下文
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private HandlerResponse<String> _executeGpt(HandlerContext<OpenAIChatHandler.Request> context) {
        try {
            // 获取请求参数
            Request request = context.getRequest();
            // 获取模型类型
            ModelTypeEnum modelType = TokenCalculator.fromName(request.getModel());
            request.setModel(modelType.getName());
            BaseLLMUsage baseLLMUsage;
            String message;
            // 执行通义千问
            if (ModelTypeEnum.QWEN.equals(modelType) || ModelTypeEnum.QWEN_MAX.equals(modelType)) {
                BaseLLMResult<GenerationResult> result = this._executeQwen(request);
                baseLLMUsage = result.getUsage();
                message = result.getText();
            }
            // 执行OpenAI
            else {
                ChatResult<ChatCompletionResult> chatResult = _executeOpenAi(request);
                baseLLMUsage = chatResult.getUsage();
                message = huandlerOpenAiMessage(chatResult, request);
            }

            // 计算价格相关数据
            Long messageTokens = baseLLMUsage.getPromptTokens();
            Long answerTokens = baseLLMUsage.getCompletionTokens();
            Long totalTokens = baseLLMUsage.getTotalTokens();
            BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(modelType, true);
            BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(modelType, false);
            BigDecimal messagePrice = TokenCalculator.getTextPrice(messageTokens, modelType, true);
            BigDecimal answerPrice = TokenCalculator.getTextPrice(answerTokens, modelType, false);
            BigDecimal totalPrice = messagePrice.add(answerPrice);

            // 构建返回结果
            HandlerResponse<String> appStepResponse = new HandlerResponse<>();
            appStepResponse.setSuccess(true);
            appStepResponse.setStepConfig(JSONUtil.toJsonStr(request));
            appStepResponse.setMessage(request.getPrompt());
            appStepResponse.setAnswer(message);
            appStepResponse.setOutput(message);
            appStepResponse.setMessageUnitPrice(messageUnitPrice);
            appStepResponse.setAnswerUnitPrice(answerUnitPrice);
            appStepResponse.setMessageTokens(messageTokens);
            appStepResponse.setAnswerTokens(answerTokens);
            appStepResponse.setTotalTokens(totalTokens);
            appStepResponse.setTotalPrice(totalPrice);
            return appStepResponse;
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("AI大模型【执行失败】: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_LLM_FAILURE, exception.getMessage(), exception);
        }
    }

    /**
     * 调用 OpenAI AI模型生成内容
     *
     * @param request 请求
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ChatResult<ChatCompletionResult> _executeOpenAi(Request request) {
        try {
            log.info("OpenAI大模型【开始执行】: {}", JSONUtil.toJsonStr(request));
            // 构建 ChatOpenAI 对象，用于调用 OpenAI 大模型
            ChatOpenAI chatOpenAI = new ChatOpenAI();
            chatOpenAI.setModel(request.getModel());
            chatOpenAI.setStream(request.getStream());
            chatOpenAI.setMaxTokens(request.getMaxTokens());
            chatOpenAI.setTemperature(request.getTemperature());
            chatOpenAI.setN(request.getN());
            chatOpenAI.addCallbackHandler(this.getStreamingSseCallBackHandler());

            // 构建系统消息提示词
            List<BaseMessage> messages = new ArrayList<>();
            String systemPrompt = request.getPrompt();
            if (StringUtils.isNotBlank(systemPrompt)) {
                SystemMessage systemMessage = new SystemMessage(systemPrompt);
                messages.add(systemMessage);
            }

            // 构建用户提示词
            String userPrompt = request.getUserPrompt();
            if (StringUtils.isNotBlank(userPrompt)) {
                HumanMessage userMessage = new HumanMessage(userPrompt);
                messages.add(userMessage);
            }

            List<List<BaseMessage>> chatMessages = Collections.singletonList(messages);

            // 执行 OpenAI 大模型
            ChatResult<ChatCompletionResult> chatResult = chatOpenAI.generate(chatMessages);

            log.info("OpenAI大模型【执行成功】: {}", JSONUtil.toJsonStr(chatResult));
            return chatResult;
        } catch (OpenAiHttpException exception) {
            log.error("OpenAI大模型【执行失败】: Http状态码: {}, 错误码: {}, 错误类型: {}, 错误消息: {}, 请求参数: {}",
                    exception.statusCode, exception.code, exception.type, exception.getMessage(), exception.param);
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_LLM_FAILURE, exception.getMessage(), exception);
        } catch (Exception exception) {
            log.error("OpenAI大模型【执行失败】: 错误消息: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_LLM_FAILURE, exception.getMessage(), exception);
        }
    }

    /**
     * 处理OpenAI返回结果
     *
     * @param chatResult OpenAI返回结果
     * @param request    请求
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String huandlerOpenAiMessage(ChatResult<ChatCompletionResult> chatResult, Request request) {
        if (request.getN() == 1) {
            return chatResult.getText();
        } else {
            List<ChatCompletionResult> collect = CollectionUtil.emptyIfNull(chatResult.getChatGenerations()).stream()
                    .map(ChatGeneration::getGenerationInfo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (CollectionUtil.isEmpty(collect)) {
                return "[]";
            } else {
                ChatCompletionResult chatCompletionResult = collect.get(0);
                List<ChatCompletionChoice> choices = CollectionUtil.emptyIfNull(chatCompletionResult.getChoices()).stream()
                        .peek(item -> item.setFinishReason(null))
                        .collect(Collectors.toList());
                return JSONUtil.toJsonStr(choices);
            }
        }
    }

    /**
     * 调用 通义千问 AI模型生成内容
     *
     * @param request 请求
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private BaseLLMResult<GenerationResult> _executeQwen(Request request) {
        try {
            log.info("通义千问大模型【开始执行】: {}", JSONUtil.toJsonStr(request));
            String prompt = request.getPrompt();

            // 构建 ChatQwen 对象，用于调用通义千问大模型
            ChatQwen chatQwen = new ChatQwen();
            chatQwen.setTopP(request.getTemperature());
            chatQwen.setModel(request.getModel());
            chatQwen.setStream(false);
            chatQwen.addCallbackHandler(this.getStreamingSseCallBackHandler());

            List<BaseMessagePromptTemplate> messages = new ArrayList<>();
            // 构建系统消息提示词
            String systemPrompt = request.getPrompt();
            if (StringUtils.isNotBlank(systemPrompt)) {
                StringPromptTemplate systemPromptTemplate = new PromptTemplate(systemPrompt, new ArrayList<>());
                SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(systemPromptTemplate);
                messages.add(systemMessagePromptTemplate);
            }

            // 构建用户消息提示词
            String userPrompt = request.getUserPrompt();
            if (StringUtils.isNotBlank(userPrompt)) {
                StringPromptTemplate userPromptTemplate = new PromptTemplate(userPrompt, new ArrayList<>());
                HumanMessagePromptTemplate userMessagePromptTemplate = new HumanMessagePromptTemplate(userPromptTemplate);
                messages.add(userMessagePromptTemplate);
            }

            ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(messages);

            // 构建 LLMChain 对象，用于调用通义千问大模型
            LLMChain<GenerationResult> llmChain = new LLMChain<>(chatQwen, chatPromptTemplate);
            llmChain.setMemory(null);

            // 执行通义千问大模型
            BaseVariable humanInput = BaseVariable.newString("input", "");
            BaseLLMResult<GenerationResult> result = llmChain.call(Collections.singletonList(humanInput));

            log.info("通义千问大模型【执行成功】: {}", JSONUtil.toJsonStr(result));
            return result;
        } catch (ApiException exception) {
            Status status = exception.getStatus();
            log.error("通义千问大模型【执行失败】: Http状态码: {}, 错误码: {}, 错误原因: {}, 使用量: {}",
                    status.getStatusCode(), status.getCode(), status.getMessage(), JsonUtils.toJsonString(status.getUsage()));
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_LLM_FAILURE, status.getMessage(), exception);
        } catch (Exception exception) {
            log.error("通义千问大模型【执行失败】: 错误原因: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_LLM_FAILURE, exception.getMessage(), exception);
        }
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

        private String userPrompt;

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
