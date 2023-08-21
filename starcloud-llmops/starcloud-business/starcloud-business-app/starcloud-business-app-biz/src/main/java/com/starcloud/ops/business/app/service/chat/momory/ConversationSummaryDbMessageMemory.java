package com.starcloud.ops.business.app.service.chat.momory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.summary.SummarizerMixin;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 带自动总结 和 message落库的 memory
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ConversationSummaryDbMessageMemory extends SummarizerMixin {

    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private static LogAppMessageService messageService = SpringUtil.getBean(LogAppMessageService.class);

    /**
     * 最大需要总结的 tokens数量，当超过次值 需要总结了
     */
    private int summaryMaxTokens;

    private ChatRequestVO chatRequestVO;

    private ChatAppEntity chatAppEntity;

    /**
     * 全量的历史表数据
     */
    private List<LogAppMessageDO> logAppMessage;


    public ConversationSummaryDbMessageMemory() {
        super();
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

        //总结用模型
        ChatOpenAI chatOpenAi = new ChatOpenAI();
        //16k 去总结
        chatOpenAi.setModel("gpt-3.5-turbo-16k");
        chatOpenAi.setMaxTokens(500);
        chatOpenAi.setTemperature(0d);

        this.setLlm(chatOpenAi);
    }

    @Override
    public List<BaseVariable> loadMemoryVariables() {

        ChatMessageHistory chatMessageHistory = this.getChatHistory();
        //需要总结
        if (this.checkNeedSummary(chatMessageHistory)) {

            List<BaseMessage> baseMessages = chatMessageHistory.limitMessage(1);
            String existingSummary = "";
            List<BaseMessage> restMessages = new ArrayList<>();

            if (CollectionUtil.isNotEmpty(baseMessages) && baseMessages.get(0) instanceof AIMessage) {
                existingSummary = baseMessages.get(0).getContent();

                //@todo 注意，总结的 prompt 数量也可能超过原始的 prompt量
                //剩余的 message + 第一的总结
                //总返回数量的控制
                //@todo 后面会改成调用 应用市场的
                int limit = 1 - CollectionUtil.size(chatMessageHistory.getMessages());
                restMessages = chatMessageHistory.limitMessage(limit);


            } else {

                //全部
                restMessages = chatMessageHistory.getMessages();

            }

            Long start = System.currentTimeMillis();
            String newLines = BaseMessage.getBufferString(restMessages);

            log.info("start summary history\n {}, {}", newLines, existingSummary);

            BaseLLMResult llmResult = this.predictNewSummary(restMessages, existingSummary);
            Long end = System.currentTimeMillis();
            log.info("success summary history, {} ms", end - start);

            //简单拼接下内容
            this.createSummaryMessage(llmResult, this.renderPrompt(restMessages, existingSummary));

            String summary = llmResult.getText();
            if (StrUtil.isNotBlank(summary)) {

                ChatMessageHistory summaryChatMessageHistory = new ChatMessageHistory();
                summaryChatMessageHistory.addMessage(new SystemMessage(summary));
                //重制一个新的
                this.setChatHistory(summaryChatMessageHistory);
            }
        }

        //对总结 message再次处理下
        if (CollectionUtil.isNotEmpty(chatMessageHistory.getMessages()) && chatMessageHistory.getMessages().get(0) instanceof AIMessage) {
            //肯定是总结message
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

        //暂存需要保存的 message
        List<BaseMessage> newMessages = new ArrayList<>();

        //手动处理不需要增加 第一条用户message
        if (baseVariables != null) {
            BaseVariable variable = getPromptInputKey(baseVariables);
            newMessages.add(new HumanMessage(String.valueOf(variable.getValue())));
        }

        List<ChatGeneration> generations = Optional.ofNullable(result.getGenerations()).orElse(new ArrayList());
        for (ChatGeneration g : generations) {
            newMessages.add(g.getChatMessage());
        }

        if (CollectionUtil.size(newMessages) == 1) {
            //函数执行记录处理
            if (newMessages.get(0) instanceof FunctionMessage) {
                FunctionMessage functionMessage = (FunctionMessage) newMessages.get(0);

                this.createFunctionCallMessage(functionMessage);

                //落盘成功后 加入到 memory
                this.getChatHistory().addMessage(functionMessage);

                return;
            }

            //llm根据函数返回结果生成最终回答
            if (newMessages.get(0) instanceof AIMessage) {
                AIMessage aiMessage = (AIMessage) newMessages.get(0);

                this.createFunDoneMessage(aiMessage);

                //落盘成功后 加入到 memory
                this.getChatHistory().addMessage(aiMessage);

                return;
            }
        } else if (CollectionUtil.size(newMessages) == 2) {

            /**
             * 遍历 message，两两组合 生成一条 DB记录
             */
            for (int i = 0; i < newMessages.size(); i++) {

                BaseMessage one = newMessages.get(i);
                BaseMessage two = newMessages.get(++i);

                if (one instanceof HumanMessage && two instanceof AIMessage) {

                    HumanMessage humanMessage = (HumanMessage) one;
                    AIMessage aiMessage = (AIMessage) two;

                    //llm 函数调用返回
                    if (two.getAdditionalArgs().get("function_call") != null) {
                        this.createChatFunctionMessage(humanMessage, aiMessage);

                        //落盘成功后 加入到 memory
                        this.getChatHistory().addMessage(humanMessage);
                        this.getChatHistory().addMessage(aiMessage);

                    } else {
                        //普通对话返回
                        this.createChatMessage(humanMessage, aiMessage);

                        //落盘成功后 加入到 memory
                        this.getChatHistory().addMessage(humanMessage);
                        this.getChatHistory().addMessage(aiMessage);
                    }
                }
            }

        } else {

            throw new IllegalArgumentException("saveContext is fail size Illegal, BaseLLMResult: " + result);
        }

        //@todo
        //benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
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

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT.name());

        });

        //结构太深，无法把messageID 返回出去，所以在这里处理权益
        benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), (long) (logVo.getMessageTokens() + logVo.getAnswerTokens()), Long.valueOf(logVo.getCreator()), logVo.getUid());
    }


    /**
     * 增加 请求LLM，LLM返回需要函数调用的一条日志
     */
    private void createChatFunctionMessage(HumanMessage humanMessage, AIMessage aiMessage) {

        String message = humanMessage.getContent();

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

        benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), (long) (logVo.getMessageTokens() + logVo.getAnswerTokens()), Long.valueOf(logVo.getCreator()), logVo.getUid());
    }


    /**
     * 增加 函数调用 日志
     */
    private void createFunctionCallMessage(FunctionMessage functionMessage) {

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

        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            messageCreateReqVO.setAppConversationUid(conversationUid);
            messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
            messageCreateReqVO.setFromScene(scene);
            messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(chatConfig));

            messageCreateReqVO.setVariables(variables);
            messageCreateReqVO.setAppStep("");

            messageCreateReqVO.setMessage(message);
            messageCreateReqVO.setMessageTokens(Math.toIntExact(messageTokens));
            messageCreateReqVO.setMessageUnitPrice(messageUnitPrice);

            messageCreateReqVO.setAnswer(answer);
            messageCreateReqVO.setAnswerTokens(Math.toIntExact(answerTokens));
            messageCreateReqVO.setAnswerUnitPrice(answerUnitPrice);

            messageCreateReqVO.setElapsed(elapsed);

            messageCreateReqVO.setTotalPrice(totalPrice);
            messageCreateReqVO.setCurrency("USD");


            messageCreateReqVO.setStatus(functionMessage.getStatus() ? "SUCCESS" : "ERROR");
            messageCreateReqVO.setCreator(userId);
            messageCreateReqVO.setEndUser(endUser);

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.FUN_CALL.name());

        });


        //工具调用权益

    }


    /**
     * 增加 LLM 函数调用完毕日志
     */
    private void createFunDoneMessage(AIMessage aiMessage) {

        String answer = aiMessage.getContent();
        Map llmParams = (Map) aiMessage.getAdditionalArgs().get("llm_params");

        //取出上一条message，是 fun_message
        ChatMessage chatMessage = (com.theokanning.openai.completion.chat.ChatMessage) CollectionUtil.getLast((ArrayList) llmParams.getOrDefault("messages", new ArrayList<>()));
        //上次函数调用的返回结果,fun执行结果
        String message = chatMessage.getContent();

        LogAppMessageCreateReqVO logVo = this.getChatAppEntity().createAppMessage((reqVo) -> {

            LogAppMessageCreateReqVO messageCreateReqVO = (LogAppMessageCreateReqVO) reqVo;

            this.updateLogAppMessageVO(aiMessage, messageCreateReqVO);

            messageCreateReqVO.setMessage(message);
            messageCreateReqVO.setAnswer(answer);

            messageCreateReqVO.setStatus("SUCCESS");

            messageCreateReqVO.setMsgType(LogMessageTypeEnum.FUN_DONE.name());

        });

        benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), (long) (logVo.getMessageTokens() + logVo.getAnswerTokens()), Long.valueOf(logVo.getCreator()), logVo.getUid());
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
        messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(chatConfig));
        messageCreateReqVO.setAppStep("");
        messageCreateReqVO.setCreator(userId);
        messageCreateReqVO.setEndUser(endUser);


        Map llmParams = (Map) baseMessage.getAdditionalArgs().getOrDefault("llm_params", new HashMap<>());
        BaseLLMUsage llmUsage = (BaseLLMUsage) baseMessage.getAdditionalArgs().getOrDefault("usage", new BaseLLMUsage());
        Long llmElapsed = (Long) baseMessage.getAdditionalArgs().getOrDefault("llm_elapsed", 0l);

        ModelType modelType = TokenCalculator.fromName(llmParams.getOrDefault("model", "").toString());

        String variables = JsonUtils.toJsonString(llmParams);

        String answer = baseMessage.getContent();

        Long messageTokens = llmUsage.getPromptTokens();
        BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(modelType, true);
        Long answerTokens = llmUsage.getCompletionTokens();
        BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(modelType, false);
        BigDecimal totalPrice = TokenCalculator.getTextPrice(messageTokens, modelType, true).add(TokenCalculator.getTextPrice(answerTokens, modelType, false));


        messageCreateReqVO.setVariables(variables);

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
        //没有历史数据
        if (CollectionUtil.isEmpty(appMessageList)) {
            return;
        }

        //取出最后一个总结
        Optional<LogAppMessageDO> summaryMessageOptional = appMessageList.stream()
                .filter(logAppMessageDO -> LogMessageTypeEnum.SUMMARY.name().equals(logAppMessageDO.getMsgType()))
                .findFirst();

        if (summaryMessageOptional.isPresent()) {
            // summaryMessage != null
            LogAppMessageDO summaryMessage = summaryMessageOptional.get();
            history.addMessage(new AIMessage(summaryMessage.getAnswer()));

            //取总结之后的 message
            appMessageList = appMessageList.stream().filter(m -> m.getId() > summaryMessage.getId()).collect(Collectors.toList());
        }

        Collections.reverse(appMessageList);
        if (CollectionUtil.isNotEmpty(appMessageList)) {
            // summaryMessage 和 summaryMessage 不会同时为空

            for (LogAppMessageDO logAppMessageDO : appMessageList) {
                if (LogMessageTypeEnum.CHAT.name().equals(logAppMessageDO.getMsgType())) {

                    history.addUserMessage(logAppMessageDO.getMessage());
                    history.addAiMessage(logAppMessageDO.getAnswer());
                }
            }
        }

    }

    private Boolean checkNeedSummary(ChatMessageHistory history) {

        if (CollectionUtil.isEmpty(history.getMessages())) {
            return false;
        }
        int messageTokens = this.calculateMaxTokens(history.getMessages());
        int maxTokens = this.getSummaryMaxTokens();

        log.info("checkNeedSummary: {} > {}", messageTokens, maxTokens);

        return messageTokens > maxTokens;
    }


    private int calculateMaxTokens(List<BaseMessage> messages) {
        String historyStr = BaseMessage.getBufferString(messages);

        //@todo 总结也不一定看模型，还要看成本，保证比较小的tokens下进行对话，所以比较的是计算后剩余可用的tokens
        Optional<ModelType> optionalModelType = ModelType.fromName(ModelType.GPT_3_5_TURBO.getName());
        return optionalModelType.map(type -> TokenUtils.intTokens(type, historyStr)).orElseGet(() -> TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, historyStr));
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


    @Deprecated
    private List<LogAppMessageDO> getLogAppMessageDO() {

        if (CollectionUtil.isEmpty(logAppMessage)) {

            LogAppMessagePageReqVO reqVO = new LogAppMessagePageReqVO();
            reqVO.setPageSize(100);
            reqVO.setPageNo(1);
            reqVO.setAppConversationUid(this.getChatRequestVO().getConversationUid());
            PageResult<LogAppMessageDO> pageResult = messageService.getAppMessagePage(reqVO);
            this.logAppMessage = Optional.ofNullable(pageResult).map(PageResult::getList).orElse(new ArrayList<>());
        }

        return this.logAppMessage;
    }

}
