package com.starcloud.ops.business.app.service.chat.momory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.summary.SummarizerMixin;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ConversationTokenDbMessageMemory extends SummarizerMixin {

    private LogAppMessageService messageService;

    private String modelType = "gpt-3.5-turbo";

    private int maxTokens;

    private ChatRequestVO chatRequestVO;

    private ChatAppEntity chatAppEntity;

    /**
     * 聊天用的 llm 为日志准备
     */
    private ChatOpenAI chatLlm;

    /**
     * 全量的历史表数据
     */
    private List<LogAppMessageDO> logAppMessage;


    public ConversationTokenDbMessageMemory() {
        super();
        this.messageService = SpringUtil.getBean(LogAppMessageService.class);
    }

    public ConversationTokenDbMessageMemory(List<LogAppMessageDO> logAppMessage, ChatRequestVO chatRequestVO, ChatAppEntity chatAppEntity) {
        super();
        this.messageService = SpringUtil.getBean(LogAppMessageService.class);
        this.logAppMessage = logAppMessage;
        this.chatRequestVO = chatRequestVO;
        this.chatAppEntity = chatAppEntity;
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
            this.createSummaryMessage(llmResult, this.renderPrompt(restMessages, existingSummary), end - start);

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
     * 存储到DB
     *
     * @param baseVariables
     * @param result
     */
    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {


        //@todo 处理 fun 调用

        BaseVariable variable = getPromptInputKey(baseVariables);

        getChatHistory().addUserMessage(String.valueOf(variable.getValue()));

        ConversationTokenDbMessageMemory memory = this;
        Optional.ofNullable(result.getGenerations()).orElse(new ArrayList()).forEach(new Consumer<ChatGeneration>() {

            @Override
            public void accept(ChatGeneration g) {

                if (g.getChatMessage() != null) {

                    String content = g.getChatMessage().getContent();
                    Map args = g.getChatMessage().getAdditionalArgs();

                    if (g.getChatMessage() instanceof AIMessage) {

                        getChatHistory().addAiMessage(content);
                        memory.createAiMessage(result);

                    } else if (g.getChatMessage() instanceof FunctionMessage) {

                        getChatHistory().addFunMessage(content, args);
                        memory.createFunctionMessage(result);

                    } else {
                        log.warn("saveContext save message is warn, Unknown message: {}", JSONUtil.toJsonStr(g));
                    }
                }
            }
        });

        //benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
    }


    private void createAiMessage(BaseLLMResult result) {

        ChatRequestVO request = this.getChatRequestVO();
        ChatConfigEntity chatConfig = this.getChatAppEntity().getChatConfig();

        Long elapsed = this.getChatLlm().getElapsed();
        String query = request.getQuery();
        String conversationUid = this.getChatRequestVO().getConversationUid();
        String scene = request.getScene();
        String userId = String.valueOf(request.getUserId());
        String endUser = this.getChatRequestVO().getEndUser();
        //总结模型的LLM参数
        Map llmParams = BeanUtil.beanToMap(this.getChatLlm());

        BigDecimal messagePrice = BigDecimal.valueOf(result.getUsage().getPromptTokens()).multiply(new BigDecimal("0.00150")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal answerPrice = BigDecimal.valueOf(result.getUsage().getCompletionTokens()).multiply(new BigDecimal("0.00200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal totalPrice = messagePrice.add(answerPrice);

        //save db
        LogAppMessageCreateReqVO messageCreateReqVO = this.getChatAppEntity().createAppMessage(new Consumer<LogAppMessageCreateReqVO>() {
            @Override
            public void accept(LogAppMessageCreateReqVO messageCreateReqVO) {

                messageCreateReqVO.setAppConversationUid(conversationUid);
                messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
                messageCreateReqVO.setFromScene(scene);

                messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(chatConfig));
                messageCreateReqVO.setVariables(JSONUtil.toJsonStr(llmParams));
                messageCreateReqVO.setAppStep("");

                messageCreateReqVO.setMessage(query);
                messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
                messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.00150"));

                messageCreateReqVO.setAnswer(result.getText());
                messageCreateReqVO.setAnswerTokens(Math.toIntExact(result.getUsage().getCompletionTokens()));
                messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.00200"));

                messageCreateReqVO.setElapsed(elapsed);

                messageCreateReqVO.setTotalPrice(totalPrice);
                messageCreateReqVO.setCurrency("USD");


                messageCreateReqVO.setStatus("SUCCESS");
                messageCreateReqVO.setCreator(userId);
                messageCreateReqVO.setEndUser(endUser);

                messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT.name());
            }
        });
    }


    private void createFunctionMessage(BaseLLMResult result) {

        ChatRequestVO request = this.getChatRequestVO();
        ChatConfigEntity chatConfig = this.getChatAppEntity().getChatConfig();

        Long elapsed = this.getChatLlm().getElapsed();
        String query = request.getQuery();
        String conversationUid = this.getChatRequestVO().getConversationUid();
        String scene = request.getScene();
        String userId = String.valueOf(request.getUserId());
        String endUser = this.getChatRequestVO().getEndUser();
        //总结模型的LLM参数
        Map llmParams = BeanUtil.beanToMap(this.getChatLlm());

        BigDecimal messagePrice = BigDecimal.valueOf(result.getUsage().getPromptTokens()).multiply(new BigDecimal("0.00150")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal answerPrice = BigDecimal.valueOf(result.getUsage().getCompletionTokens()).multiply(new BigDecimal("0.00200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal totalPrice = messagePrice.add(answerPrice);

        //save db
        LogAppMessageCreateReqVO messageCreateReqVO = this.getChatAppEntity().createAppMessage(new Consumer<LogAppMessageCreateReqVO>() {
            @Override
            public void accept(LogAppMessageCreateReqVO messageCreateReqVO) {

                messageCreateReqVO.setAppConversationUid(conversationUid);
                messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
                messageCreateReqVO.setFromScene(scene);

                messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(chatConfig));
                messageCreateReqVO.setVariables(JSONUtil.toJsonStr(llmParams));
                messageCreateReqVO.setAppStep("");

                messageCreateReqVO.setMessage(query);
                messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
                messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.00150"));

                messageCreateReqVO.setAnswer(result.getText());
                messageCreateReqVO.setAnswerTokens(Math.toIntExact(result.getUsage().getCompletionTokens()));
                messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.00200"));

                messageCreateReqVO.setElapsed(elapsed);

                messageCreateReqVO.setTotalPrice(totalPrice);
                messageCreateReqVO.setCurrency("USD");


                messageCreateReqVO.setStatus("SUCCESS");
                messageCreateReqVO.setCreator(userId);
                messageCreateReqVO.setEndUser(endUser);

                messageCreateReqVO.setMsgType(LogMessageTypeEnum.CHAT_FUN.name());
            }
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
        int maxTokens = this.getMaxTokens();

        log.info("checkNeedSummary: {} > {}", messageTokens, maxTokens);

        return messageTokens > maxTokens;
    }


    private int calculateMaxTokens(List<BaseMessage> messages) {
        String historyStr = BaseMessage.getBufferString(messages);

        //@todo sdk 要更新
        Optional<ModelType> optionalModelType = ModelType.fromName(this.getModelType());
        return optionalModelType.map(type -> TokenUtils.intTokens(type, historyStr)).orElseGet(() -> TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, historyStr));
    }


    /**
     * @param result
     * @param query
     * @param elapsed
     * @todo 要更新SDK，按照实际模型的价格去计算
     */
    private void createSummaryMessage(BaseLLMResult result, String query, Long elapsed) {

        ChatRequestVO request = this.getChatRequestVO();

        String conversationUid = this.getChatRequestVO().getConversationUid();
        String scene = request.getScene();
        String userId = String.valueOf(request.getUserId());
        String endUser = this.getChatRequestVO().getEndUser();
        //总结模型的LLM参数
        Map llmParams = BeanUtil.beanToMap(this.getLlm());


        BigDecimal messagePrice = BigDecimal.valueOf(result.getUsage().getPromptTokens()).multiply(new BigDecimal("0.00300")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal answerPrice = BigDecimal.valueOf(result.getUsage().getCompletionTokens()).multiply(new BigDecimal("0.00400")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        BigDecimal totalPrice = messagePrice.add(answerPrice);

        this.getChatAppEntity().createAppMessage(new Consumer<LogAppMessageCreateReqVO>() {
            @Override
            public void accept(LogAppMessageCreateReqVO messageCreateReqVO) {

                messageCreateReqVO.setAppConversationUid(conversationUid);
                messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
                messageCreateReqVO.setFromScene(scene);

                messageCreateReqVO.setAppConfig("{}");
                messageCreateReqVO.setVariables(JSONUtil.toJsonStr(llmParams));
                messageCreateReqVO.setAppStep("");

                messageCreateReqVO.setMessage(query);
                messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
                messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.00300"));

                messageCreateReqVO.setAnswer(result.getText());
                messageCreateReqVO.setAnswerTokens(Math.toIntExact(result.getUsage().getCompletionTokens()));
                messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.00400"));

                messageCreateReqVO.setElapsed(elapsed);

                messageCreateReqVO.setTotalPrice(totalPrice);
                messageCreateReqVO.setCurrency("USD");


                messageCreateReqVO.setStatus("SUCCESS");
                messageCreateReqVO.setCreator(userId);
                messageCreateReqVO.setEndUser(endUser);

                messageCreateReqVO.setMsgType(LogMessageTypeEnum.SUMMARY.name());
            }
        });

    }
}
