package com.starcloud.ops.business.app.service.chat.momory;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ConversationTokenDbBufferMemory extends BaseChatMemory {

    private LogAppMessageService messageService;

    private int maxTokens;

    private String conversationUid;

    private String modelType;

    public ConversationTokenDbBufferMemory(int maxTokens, String conversationUid, String modelType) {
        this.messageService = SpringUtil.getBean(LogAppMessageService.class);
        this.maxTokens = maxTokens;
        this.conversationUid = conversationUid;
        this.modelType = modelType;
    }

    @Override
    public List<BaseVariable> loadMemoryVariables() {
        LogAppMessagePageReqVO reqVO = new LogAppMessagePageReqVO();
        reqVO.setPageSize(100);
        reqVO.setPageNo(1);
        reqVO.setAppConversationUid(conversationUid);
        PageResult<LogAppMessageDO> pageResult = messageService.getAppMessagePage(reqVO);
        List<LogAppMessageDO> appMessageList = Optional.ofNullable(pageResult).map(PageResult::getList).orElse(new ArrayList<>());

        if (CollectionUtils.isEmpty(appMessageList)) {
            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(BaseMessage.getBufferString(new ArrayList<>()))
                    .build());
        }

        Optional<LogAppMessageDO> summaryMessageOptional = appMessageList.stream()
                .filter(logAppMessageDO -> AppSceneEnum.SYSTEM_SUMMARY.name().equals(logAppMessageDO.getFromScene()))
                .findFirst();

        LogAppMessageDO summaryMessage;
        if (summaryMessageOptional.isPresent()) {
            // summaryMessage != null
            summaryMessage = summaryMessageOptional.get();
            appMessageList = appMessageList.stream().filter(m -> m.getId() > summaryMessage.getId()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(appMessageList)) {
                // summaryMessage 和 summaryMessage 不会同时为空
                ChatMessageHistory historySummary = new ChatMessageHistory();
                historySummary.addMessage(new SystemMessage(summaryMessage.getAnswer()));
                super.setChatHistory(historySummary);
                return Collections.singletonList(BaseVariable.builder()
                        .field(MEMORY_KEY)
                        .value(BaseMessage.getBufferString(historySummary.getMessages()))
                        .build());
            }
            appMessageList.add(summaryMessage);
        }


        Collections.reverse(appMessageList);
        ChatMessageHistory history = new ChatMessageHistory();
        for (LogAppMessageDO logAppMessageDO : appMessageList) {
            if (AppSceneEnum.SYSTEM_SUMMARY.name().equals(logAppMessageDO.getFromScene())) {
                history.addMessage(new SystemMessage(logAppMessageDO.getAnswer()));
            } else {
                history.addUserMessage(logAppMessageDO.getMessage());
                history.addAiMessage(logAppMessageDO.getAnswer());
            }
        }

        if (calculateMaxTokens(history.getMessages()) > maxTokens) {

            // summaryMessage newMessages 总结
            Collections.reverse(appMessageList);
            String summary = summaryHistory(BaseMessage.getBufferString(history.getMessages()));
            ChatMessageHistory historySummary = new ChatMessageHistory();
            historySummary.addMessage(new SystemMessage(summary));
            super.setChatHistory(historySummary);
            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(BaseMessage.getBufferString(historySummary.getMessages()))
                    .build());

        }
        super.setChatHistory(history);
        return Collections.singletonList(BaseVariable.builder()
                .field(MEMORY_KEY)
                .value(BaseMessage.getBufferString(history.getMessages()))
                .build());

    }


    private int calculateMaxTokens(List<BaseMessage> messages) {
        String historyStr = BaseMessage.getBufferString(messages);

        Optional<ModelType> optionalModelType = ModelType.fromName(modelType);
        return optionalModelType.map(type -> TokenUtils.intTokens(type, historyStr)).orElseGet(() -> TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, historyStr));

    }

    private String summaryHistory(String bufferString) {
        Long start = System.currentTimeMillis();
        ChatOpenAI openAi = new ChatOpenAI();
        ModelType.fromName(modelType).ifPresent(type -> openAi.setModel(type.getName()));
        List<BaseMessage> messages = new ArrayList<>();
        String query = String.format(PromptTempletEnum.HISTORY_SUMMARY.getTemp(),
                bufferString);

        HumanMessage humanMessage = new HumanMessage(query);
        messages.add(humanMessage);
        log.info("start summary history {}", messages);
        ChatResult<ChatCompletionResult> openaiResult = openAi._generate(messages, null, null, null);
        Long end = System.currentTimeMillis();
        log.info("success summary history, {} ms", end - start);
        BigDecimal totalPrice = BigDecimal.valueOf(openaiResult.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();
        messageCreateReqVO.setUid(IdUtil.fastSimpleUUID());
        messageCreateReqVO.setAppConversationUid(conversationUid);
        messageCreateReqVO.setMessage(query);
        messageCreateReqVO.setMessageTokens(Math.toIntExact(openaiResult.getUsage().getPromptTokens()));
        messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.0200"));
        messageCreateReqVO.setAppConfig("{}");
        messageCreateReqVO.setVariables("{}");
        messageCreateReqVO.setAppUid("SYSTEM_SUMMARY");
        messageCreateReqVO.setAppStep("");
        messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
        messageCreateReqVO.setAnswerTokens(Math.toIntExact(openaiResult.getUsage().getCompletionTokens()));
        messageCreateReqVO.setAnswer(openaiResult.getText());
        messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.0200"));
        messageCreateReqVO.setElapsed(end - start);
        messageCreateReqVO.setTotalPrice(totalPrice);
        messageCreateReqVO.setCurrency("USD");
        messageCreateReqVO.setFromScene(AppSceneEnum.SYSTEM_SUMMARY.name());
        messageCreateReqVO.setStatus("SUCCESS");
        messageService.createAppMessage(messageCreateReqVO);
        return openaiResult.getText();
    }
}
