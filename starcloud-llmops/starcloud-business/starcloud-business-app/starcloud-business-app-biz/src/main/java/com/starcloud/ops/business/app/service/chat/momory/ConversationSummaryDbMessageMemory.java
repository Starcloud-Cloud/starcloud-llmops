package com.starcloud.ops.business.app.service.chat.momory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.ChatErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.agent.base.action.FunctionsAgentAction;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.summary.SummarizerMixin;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 带自动总结 和 message落库的 memory
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ConversationSummaryDbMessageMemory extends SummarizerMixin {

    private static LogAppMessageService messageService = SpringUtil.getBean(LogAppMessageService.class);

    private static AdminUserRightsApi adminUserRightsApi = SpringUtil.getBean(AdminUserRightsApi.class);

    private ChatRequestVO chatRequestVO;

    @JsonIgnore
    @JSONField(serialize = false)
    private ChatAppEntity chatAppEntity;

    private ChatConfigEntity chatConfig;

    /**
     * 全量的历史表数据
     */
    private List<LogAppMessageDO> logAppMessage = new ArrayList<>();

    /**
     * 上下文文档历史
     */
    private MessageContentDocMemory messageContentDocMemory;

    public ConversationSummaryDbMessageMemory() {
        super();
        this.init();
    }

    public ConversationSummaryDbMessageMemory(List<LogAppMessageDO> logAppMessage) {
        super();
        this.logAppMessage = logAppMessage;
        this.init();
    }

    /**
     * 初始化历史message
     */
    private void init() {

        List<LogAppMessageDO> appMessageList = this.getLogAppMessage();
        this.initMessageHistory(appMessageList);

        // 总结用模型
        ChatOpenAI chatOpenAi = new ChatOpenAI();
        // 16k 去总结
        chatOpenAi.setModel(ModelTypeEnum.GPT_4_TURBO.getName());
        chatOpenAi.setMaxTokens(400);
        chatOpenAi.setTemperature(0d);

        this.setLlm(chatOpenAi);
    }

    /**
     * 初始化文档历史
     */
    public void initContentDocMemory() {
        this.messageContentDocMemory = new MessageContentDocMemory(this);
    }

    @Override
    public List<BaseVariable> loadMemoryVariables() {

        ChatMessageHistory chatMessageHistory = this.getChatHistory();
        // 需要总结
        if (this.checkNeedSummary(chatMessageHistory)) {

            List<BaseMessage> baseMessages = chatMessageHistory.limitMessage(1);
            String existingSummary = "";
            List<BaseMessage> restMessages = new ArrayList<>();

            //@todo 这里假设第一个是 总结，需要补上标识
            if (CollectionUtil.isNotEmpty(baseMessages) && baseMessages.get(0) instanceof AIMessage) {
                existingSummary = baseMessages.get(0).getContent();

                // 最后一次总结 + 剩余的 message
                //@todo 后面会改成调用 应用市场的总结应用
                int limit = 1 - CollectionUtil.size(chatMessageHistory.getMessages());
                restMessages = chatMessageHistory.limitMessage(limit);


            } else {

                // 全部
                restMessages = chatMessageHistory.getMessages();

            }

            Long start = System.currentTimeMillis();
            String newLines = BaseMessage.getBufferString(restMessages);

            log.info("start summary history\nnewLines:\n{}\n\nexistingSummary:\n{}\n\n", newLines, existingSummary);

            ChatOpenAI chatOpenAI = (ChatOpenAI) this.getLlm();
            BaseLLMResult llmResult = this.predictNewSummary(restMessages, existingSummary, chatOpenAI.getMaxTokens());
            Long end = System.currentTimeMillis();

            if (llmResult == null) {
                log.error("summary history is fail:{}, {}", llmResult, restMessages);
                throw ServiceExceptionUtil.exception(ChatErrorCodeConstants.MEMORY_SUMMARY_ERROR);
            }

            log.info("success summary history, {} ms", end - start);
            // 简单拼接下内容
            // 因为message太长了，只好取上一次的总结内容

            this.createSummaryMessage(llmResult, existingSummary);
            String summary = llmResult.getText();
            if (StrUtil.isNotBlank(summary)) {

                log.info("summary: {}", summary);

                ChatMessageHistory summaryChatMessageHistory = new ChatMessageHistory();
                summaryChatMessageHistory.addMessage(new SystemMessage(summary));
                // 重制一个新的
                this.setChatHistory(summaryChatMessageHistory);
            }
        }

        //  //@todo 这里假设第一个是 总结，需要补上标识，对总结 message再次处理下
        if (CollectionUtil.isNotEmpty(chatMessageHistory.getMessages()) && chatMessageHistory.getMessages().get(0) instanceof AIMessage) {
            // 肯定是总结message
            chatMessageHistory.getMessages().get(0).setContent("Summary of the previous conversation ```" + chatMessageHistory.getMessages().get(0).getContent() + "```");
        }

        String historyStr = BaseMessage.getBufferString(this.getChatHistory().getMessages());

        return Collections.singletonList(BaseVariable.builder()
                .field(MEMORY_KEY)
                .value(historyStr)
                .build());

    }


    /**
     * 存储到DB，有4中入参情况
     * 1，llm普通对话
     * 2，llm函数返回
     * 3，函数执行
     * 4，LLM调用最终返回
     *
     * @param baseVariables
     * @param result
     */
    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        if (baseVariables != null) {
            this._saveChatContext(baseVariables, result);
        } else {
            this._saveChatToolCallContext(result);
        }
    }


    /**
     * 工具对话 保存
     *
     * @param result
     */
    private void _saveChatToolCallContext(BaseLLMResult result) {

        // 只支持传入一个
        if (CollectionUtil.size(result.getGenerations()) > 2) {
            throw new IllegalArgumentException("saveContext is fail size Illegal: " + CollectionUtil.size(result.getGenerations()));
        }

        if (CollectionUtil.size(result.getGenerations()) == 2) {

            ChatGeneration humanGeneration = (ChatGeneration) result.getGenerations().get(0);
            ChatGeneration aiGeneration1 = (ChatGeneration) result.getGenerations().get(1);

            if (humanGeneration.getChatMessage() instanceof HumanMessage && aiGeneration1.getChatMessage() instanceof AIMessage) {
                HumanMessage humanMessage = (HumanMessage) humanGeneration.getChatMessage();
                AIMessage aiMessage = (AIMessage) aiGeneration1.getChatMessage();

                // llm返回函数调用
                if (aiMessage.getAdditionalArgs().get("function_call") != null) {
                    this.createChatFunctionMessage(humanMessage.getContent(), aiMessage);

                    // 落盘成功后 加入到 memory
                    this.getChatHistory().addMessage(humanMessage);
                    this.getChatHistory().addMessage(aiMessage);

                } else {
                    // 普通对话返回
                    this.createChatMessage(humanMessage, aiMessage);

                    // 落盘成功后 加入到 memory
                    this.getChatHistory().addMessage(humanMessage);
                    this.getChatHistory().addMessage(aiMessage);
                }
            }


        } else if (CollectionUtil.size(result.getGenerations()) == 1) {
            ChatGeneration chatGeneration = (ChatGeneration) result.getGenerations().get(0);
            BaseMessage currentMessage = chatGeneration.getChatMessage();

            // 函数执行记录
            if (currentMessage instanceof FunctionMessage) {
                FunctionMessage functionMessage = (FunctionMessage) currentMessage;

                this.createFunctionCallMessage(chatGeneration, functionMessage);

                // 落盘成功后 加入到 memory
                this.getChatHistory().addMessage(functionMessage);

            }

            // llm根据函数返回结果生成最终回答
            if (currentMessage instanceof AIMessage) {
                AIMessage aiMessage = (AIMessage) currentMessage;

                // 多次llm返回fun_call
                if (aiMessage.getAdditionalArgs().get("function_call") != null) {

                    this.createChatFunctionMessage("", aiMessage);

                } else {

                    this.createFunDoneMessage(aiMessage);
                }

                // 落盘成功后 加入到 memory
                this.getChatHistory().addMessage(aiMessage);
            }
        }

    }


    /**
     * 普通对话保存
     *
     * @param variables
     * @param result
     */
    private void _saveChatContext(List<BaseVariable> variables, BaseLLMResult result) {

        BaseVariable variable = BaseVariable.findVariable(variables, INPUT_KEY);
        HumanMessage humanMessage = new HumanMessage(String.valueOf(variable.getValue()));

        ChatGeneration chatGeneration = (ChatGeneration) result.getGenerations().get(0);
        AIMessage aiMessage = (AIMessage) chatGeneration.getChatMessage();

        // 普通对话返回
        this.createChatMessage(humanMessage, aiMessage);

        // 落盘成功后 加入到 memory
        this.getChatHistory().addMessage(humanMessage);
        this.getChatHistory().addMessage(aiMessage);
    }

    /**
     * 增加 普通LLM调用和返回
     */
    private void createChatMessage(HumanMessage humanMessage, AIMessage aiMessage) {

        ChatRequestVO request = this.getChatRequestVO();
        String message = humanMessage.getContent();

        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            this.updateLogAppMessageVO(aiMessage, messageCreateReqVO);

            messageCreateReqVO.setMessage(message);
            messageCreateReqVO.setStatus("SUCCESS");
            messageCreateReqVO.setMediumUid(request.getMediumUid());

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT.name());

        });

        // 结构太深，无法把messageID 返回出去，所以在这里处理权益
        Map llmParams = (Map) aiMessage.getAdditionalArgs().getOrDefault("llm_params", new HashMap<>());
        String model = llmParams.getOrDefault("model", "").toString();
        adminUserRightsApi.reduceRights(Long.valueOf(logVo.getCreator()), null, null, AdminUserRightsTypeEnum.MAGIC_BEAN, computationalPower(model, logVo.getMessageTokens() + logVo.getAnswerTokens()).intValue(), UserRightSceneUtils.getUserRightsBizType(logVo.getFromScene()).getType(), logVo.getUid());
    }

    private void createChatFunctionMessage(String message, AIMessage aiMessage) {

        ChatFunctionCall chatFunctionCall = (ChatFunctionCall) aiMessage.getAdditionalArgs().get("function_call");
        String answer = JsonUtils.toJsonString(chatFunctionCall);

        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            this.updateLogAppMessageVO(aiMessage, messageCreateReqVO);

            messageCreateReqVO.setMessage(message);
            messageCreateReqVO.setAnswer(answer);

            messageCreateReqVO.setStatus("SUCCESS");

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT_FUN.name());

        });

        Map llmParams = (Map) aiMessage.getAdditionalArgs().getOrDefault("llm_params", new HashMap<>());
        String model = llmParams.getOrDefault("model", "").toString();
        adminUserRightsApi.reduceRights(Long.valueOf(logVo.getCreator()), null, null, AdminUserRightsTypeEnum.MAGIC_BEAN, computationalPower(model, logVo.getMessageTokens() + logVo.getAnswerTokens()).intValue(), UserRightSceneUtils.getUserRightsBizType(logVo.getFromScene()).getType(), logVo.getUid());
    }


    /**
     * 增加 函数调用 日志
     */
    private void createFunctionCallMessage(ChatGeneration generation, FunctionMessage functionMessage) {

        ChatRequestVO request = this.getChatRequestVO();
        ChatConfigEntity chatConfig = this.getChatAppEntity().getChatConfig();

        String conversationUid = this.getChatRequestVO().getConversationUid();
        String scene = request.getScene();
        String userId = String.valueOf(request.getUserId());
        String endUser = this.getChatRequestVO().getEndUser();
        String message = functionMessage.getName();

        String answer = functionMessage.getContent();
        Long elapsed = functionMessage.getElapsed();
        String variables = JsonUtils.toJsonString(functionMessage.getArguments());


        Long messageTokens = 0l;
        BigDecimal messageUnitPrice = BigDecimal.ZERO;
        Long answerTokens = 0l;
        BigDecimal answerUnitPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 获取技能执行的LLM相关统计（WebSearch2Doc 已经有了）
        if (generation.getGenerationInfo() != null && generation.getGenerationInfo() instanceof FunctionsAgentAction) {
            FunctionsAgentAction functionsAgentAction = (FunctionsAgentAction) generation.getGenerationInfo();
            if (functionsAgentAction.getToolResponse() != null && functionsAgentAction.getToolResponse() instanceof HandlerResponse) {
                HandlerResponse handlerResponse = (HandlerResponse) functionsAgentAction.getToolResponse();

                messageTokens = handlerResponse.getMessageTokens();
                messageUnitPrice = handlerResponse.getMessageUnitPrice();

                answerTokens = handlerResponse.getAnswerTokens();
                answerUnitPrice = handlerResponse.getAnswerUnitPrice();

                totalPrice = handlerResponse.getTotalPrice();
            }
        }


        Long finalMessageTokens = messageTokens;
        BigDecimal finalMessageUnitPrice = messageUnitPrice;
        Long finalAnswerTokens = answerTokens;
        BigDecimal finalAnswerUnitPrice = answerUnitPrice;
        BigDecimal finalTotalPrice = totalPrice;
        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            messageCreateReqVO.setAppConversationUid(conversationUid);
            messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
            messageCreateReqVO.setFromScene(scene);
            messageCreateReqVO.setAppConfig(JsonUtils.toJsonString(chatConfig));

            messageCreateReqVO.setVariables(variables);
            messageCreateReqVO.setAppStep(AppModelEnum.CHAT.name());

            messageCreateReqVO.setMessage(message);
            messageCreateReqVO.setMessageTokens(Math.toIntExact(finalMessageTokens));
            messageCreateReqVO.setMessageUnitPrice(finalMessageUnitPrice);

            messageCreateReqVO.setAnswer(answer);
            messageCreateReqVO.setAnswerTokens(Math.toIntExact(finalAnswerTokens));
            messageCreateReqVO.setAnswerUnitPrice(finalAnswerUnitPrice);

            messageCreateReqVO.setElapsed(elapsed);

            messageCreateReqVO.setTotalPrice(finalTotalPrice);
            messageCreateReqVO.setCurrency("USD");


            messageCreateReqVO.setStatus(functionMessage.getStatus() ? "SUCCESS" : "ERROR");

            messageCreateReqVO.setCreator(userId);
            messageCreateReqVO.setEndUser(endUser);
            messageCreateReqVO.setDeptId(this.getChatAppEntity().obtainDeptId(request.getUserId()));
            messageCreateReqVO.setMsgType(LogMessageTypeEnum.FUN_CALL.name());

        });


        // 工具调用权益

    }


    /**
     * 增加 LLM 函数调用完毕日志
     */
    private void createFunDoneMessage(AIMessage aiMessage) {

        String answer = aiMessage.getContent();
        Map llmParams = (Map) aiMessage.getAdditionalArgs().get("llm_params");

        // 取出上一条message，是 fun_message
        ChatMessage chatMessage = (com.theokanning.openai.completion.chat.ChatMessage) CollectionUtil.getLast((ArrayList) llmParams.getOrDefault("messages", new ArrayList<>()));
        // 上次函数调用的返回结果,fun执行结果
        String message = String.valueOf(aiMessage.getAdditionalArgs().getOrDefault(AgentExecutor.AgentFinishInputKey, chatMessage.getContent()));

        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            this.updateLogAppMessageVO(aiMessage, messageCreateReqVO);

            // 无意义
            messageCreateReqVO.setMessage("");
            messageCreateReqVO.setAnswer(answer);

            messageCreateReqVO.setStatus("SUCCESS");

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT_DONE.name());

        });
        String model = llmParams.getOrDefault("model", "").toString();
        adminUserRightsApi.reduceRights(Long.valueOf(logVo.getCreator()), null, null, AdminUserRightsTypeEnum.MAGIC_BEAN, computationalPower(model, logVo.getMessageTokens() + logVo.getAnswerTokens()).intValue(), UserRightSceneUtils.getUserRightsBizType(logVo.getFromScene()).getType(), logVo.getUid());
    }


    /**
     * 填充LLMtokens 价格，耗时 等字段
     *
     * @param baseMessage
     * @param messageCreateReqVO
     */
    private void updateLogAppMessageVO(BaseMessage baseMessage, LogAppMessageCreateReqVO messageCreateReqVO) {


        ChatRequestVO request = this.getChatRequestVO();
        ChatConfigEntity chatConfig = this.getChatAppEntity().getChatConfig();

        String conversationUid = this.getChatRequestVO().getConversationUid();
        String scene = request.getScene();
        String userId = String.valueOf(request.getUserId());
        String endUser = this.getChatRequestVO().getEndUser();

        messageCreateReqVO.setAppConversationUid(conversationUid);
        messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
        messageCreateReqVO.setFromScene(scene);
        messageCreateReqVO.setAppConfig(JsonUtils.toJsonString(chatConfig));
        messageCreateReqVO.setAppStep(AppModelEnum.CHAT.name());
        messageCreateReqVO.setCreator(userId);
        messageCreateReqVO.setEndUser(endUser);
        messageCreateReqVO.setDeptId(this.getChatAppEntity().obtainDeptId(request.getUserId()));

        Map llmParams = (Map) baseMessage.getAdditionalArgs().getOrDefault("llm_params", new HashMap<>());
        BaseLLMUsage llmUsage = (BaseLLMUsage) baseMessage.getAdditionalArgs().getOrDefault("usage", new BaseLLMUsage());
        Long llmElapsed = (Long) baseMessage.getAdditionalArgs().getOrDefault("llm_elapsed", 0l);

        String variables = JsonUtils.toJsonString(llmParams);

        String answer = baseMessage.getContent();

        ModelTypeEnum modelType = TokenCalculator.fromName(llmParams.getOrDefault("model", "").toString());
        Long messageTokens = llmUsage.getPromptTokens();
        BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(modelType, true);
        Long answerTokens = llmUsage.getCompletionTokens();
        BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(modelType, false);
        BigDecimal totalPrice = TokenCalculator.getTextPrice(messageTokens, modelType, true).add(TokenCalculator.getTextPrice(answerTokens, modelType, false));


        messageCreateReqVO.setVariables(variables);
        messageCreateReqVO.setAiModel(modelType.getName());
        Long costPoints = computationalPower(modelType.getName(), Math.toIntExact(answerTokens) + Math.toIntExact(messageTokens));
        messageCreateReqVO.setCostPoints(costPoints.intValue());

        messageCreateReqVO.setMessageTokens(Math.toIntExact(messageTokens));
        messageCreateReqVO.setMessageUnitPrice(messageUnitPrice);

        messageCreateReqVO.setAnswer(answer);
        messageCreateReqVO.setAnswerTokens(Math.toIntExact(answerTokens));
        messageCreateReqVO.setAnswerUnitPrice(answerUnitPrice);

        messageCreateReqVO.setElapsed(llmElapsed);
        messageCreateReqVO.setTotalPrice(totalPrice);
        messageCreateReqVO.setCurrency("USD");

    }

    /**
     * 初始化 message to history
     *
     * @param appMessageList
     */
    private void initMessageHistory(List<LogAppMessageDO> appMessageList) {

        ChatMessageHistory history = this.getChatHistory();
        // 没有历史数据
        if (CollectionUtil.isEmpty(appMessageList)) {
            return;
        }

        // 取出最后一个总结
        Optional<LogAppMessageDO> summaryMessageOptional = appMessageList.stream()
                .filter(logAppMessageDO -> LogMessageTypeEnum.SUMMARY.name().equals(logAppMessageDO.getMsgType()))
                .findFirst();

        if (summaryMessageOptional.isPresent()) {
            // summaryMessage != null
            LogAppMessageDO summaryMessage = summaryMessageOptional.get();
            history.addMessage(new AIMessage(summaryMessage.getAnswer()));

            // 取总结之后的 message
            appMessageList = appMessageList.stream().filter(m -> m.getId() > summaryMessage.getId()).collect(Collectors.toList());
        }

        Collections.reverse(appMessageList);
        if (CollectionUtil.isNotEmpty(appMessageList)) {
            // summaryMessage 和 summaryMessage 不会同时为空

            for (LogAppMessageDO logAppMessageDO : appMessageList) {

                // 这里只初始化了 普通聊天内容
                if (LogMessageTypeEnum.CHAT.name().equals(logAppMessageDO.getMsgType())) {

                    history.addUserMessage(logAppMessageDO.getMessage());
                    history.addAiMessage(logAppMessageDO.getAnswer());
                }

                // llm激活函数调用
                if (LogMessageTypeEnum.CHAT_FUN.name().equals(logAppMessageDO.getMsgType())) {

                    // 还有二次调用的情况，只能在这判断。如果有说明是第一次，要增加userMessage
                    if (StrUtil.isNotBlank(logAppMessageDO.getMessage())) {
                        history.addUserMessage(logAppMessageDO.getMessage());
                    }

                    AIMessage aiMessage = new AIMessage("");
//                    Map params = JSONUtil.toBean(logAppMessageDO.getAnswer(), Map.class);

                    ChatFunctionCall call = JsonUtils.parseObject(logAppMessageDO.getAnswer(), ChatFunctionCall.class);
                    aiMessage.getAdditionalArgs().putAll(new HashMap() {{
                        put("function_call", call);
                    }});

                    history.addMessage(aiMessage);
                }

                // 函数调用
                if (LogMessageTypeEnum.FUN_CALL.name().equals(logAppMessageDO.getMsgType())) {

                    String tool = logAppMessageDO.getMessage();
                    FunctionMessage functionMessage = new FunctionMessage(tool, logAppMessageDO.getAnswer());
                    functionMessage.setArguments(logAppMessageDO.getVariables());
                    functionMessage.setStatus("SUCCESS".equals(logAppMessageDO.getStatus()));
                    functionMessage.setElapsed(logAppMessageDO.getElapsed());
                    history.addMessage(functionMessage);
                }

                // LLM根据函数结果回答
                if (LogMessageTypeEnum.CHAT_DONE.name().equals(logAppMessageDO.getMsgType())) {

                    AIMessage aiMessage = new AIMessage(logAppMessageDO.getAnswer());
                    history.addMessage(aiMessage);
                }

            }
        }
    }

    private Boolean checkNeedSummary(ChatMessageHistory history) {

        if (CollectionUtil.isEmpty(history.getMessages())) {
            return false;
        }

        String historyStr = BaseMessage.getBufferString(history.getMessages());

        int messageTokens = SummarizerMixin.calculateTokens(historyStr);
        int maxTokens = this.getSummaryMaxTokens();

        log.info("checkNeedSummary: {} > {}", messageTokens, maxTokens);

        return messageTokens > maxTokens;
    }


    private Long computationalPower(String modelType) {
        ModelTypeEnum modelTypeEnum = TokenCalculator.fromName(modelType);
        return ModelTypeEnum.GPT_4_TURBO.equals(modelTypeEnum) ? 15L : 1L;
    }

    private Long calculationTokens(Integer token) {
        if (token < 1000) {
            return 1L;
        }
        BigDecimal bigDecimal = new BigDecimal(token);
        return bigDecimal.divide(new BigDecimal(500), 0, RoundingMode.FLOOR).longValue();
    }

    private Long computationalPower(String modelType, Integer tokens) {
        return Long.valueOf(CostPointUtils.obtainChatMagicBeanCostPoint(modelType, Long.valueOf(tokens)));
    }

    /**
     * @param result
     * @param query
     * @todo 要更新SDK，按照实际模型的价格去计算
     */
    private void createSummaryMessage(BaseLLMResult result, String query) {

        ChatGeneration chatGeneration = (ChatGeneration) result.getGenerations().get(0);
        AIMessage aiMessage = (AIMessage) chatGeneration.getChatMessage();

        this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            this.updateLogAppMessageVO(aiMessage, messageCreateReqVO);

            messageCreateReqVO.setMessage(query);
            messageCreateReqVO.setAnswer(aiMessage.getContent());

            messageCreateReqVO.setStatus("SUCCESS");

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.SUMMARY.name());

        });
    }

}
