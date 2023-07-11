package com.starcloud.ops.business.chat.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.UserInputFromEntity;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.chat.enums.PromptTempletEnum;
import com.starcloud.ops.business.chat.request.ChatRequest;
import com.starcloud.ops.business.chat.service.ChatService;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author starcloud
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private LogAppConversationMapper appConversationMapper;

    @Resource
    private LogAppMessageMapper messageMapper;

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private UserBenefitsService benefitsService;

    @Override
    public List<LogAppMessageDO> chatHistory(String conversationUid) {
        LambdaQueryWrapper<LogAppMessageDO> wrapper = Wrappers.lambdaQuery(LogAppMessageDO.class)
                .eq(LogAppMessageDO::getAppConversationUid, conversationUid)
                .orderByDesc(LogAppMessageDO::getCreateTime);
        return messageMapper.selectList(wrapper);
    }

    @Override
    public SseEmitter chat(ChatRequest request) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        benefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), userId);
        long start = System.currentTimeMillis();
        LogAppConversationDO conversationDO = appConversationMapper.selectOne(LogAppConversationDO::getUid, request.getConversationId());
        AppEntity appEntity = JSON.parseObject(conversationDO.getAppConfig(), AppEntity.class);
        ChatConfigEntity chatConfig = appEntity.getChatConfig();
        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, String> cleanInputs = getCleanInputs(request.getInputs(), chatConfig.getUserInputForm());
        ChatMessageHistory history = preHistory(conversationDO.getUid(), AppModelEnum.CHAT.name());
        StringJoiner messageTemp = new StringJoiner(StringUtils.LF);

        // 配置项
        if (cleanInputs.size() > 0) {
            messageTemp.add(chatConfig.getPrePrompt());
        }

        // 数据集
        SimilarQueryRequest similarQueryRequest = new SimilarQueryRequest();
        similarQueryRequest.setQuery(request.getQuery());
        similarQueryRequest.setK(2L);
        similarQueryRequest.setDatasetUid(chatConfig.getDatasetUid());
        List<String> context = documentSegmentsService.similarQuery(similarQueryRequest);
        Map<String, Object> humanInput = new HashMap<>(cleanInputs);
        if (!CollectionUtils.isEmpty(context)) {
            messageTemp.add(PromptTempletEnum.DATASET_CONTEXT.getTemp());
            StringJoiner contextSj = new StringJoiner(StringUtils.LF);
            for (String c : context) {
                contextSj.add(c);
            }
            humanInput.put(PromptTempletEnum.DATASET_CONTEXT.getKey(), contextSj.toString());
        }

        // 历史记录
        if (history.limitMessage(1).size() > 0) {
            messageTemp.add(PromptTempletEnum.HISTORY_TEMP.getTemp());
        }
        humanInput.put(PromptTempletEnum.INPUT_TEMP.getKey(), request.getQuery());
        messageTemp.add(PromptTempletEnum.INPUT_TEMP.getTemp());

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Collections.singletonList(
                        HumanMessagePromptTemplate.fromTemplate(messageTemp.toString(), request.getQuery())
                )
        );

        SseEmitter emitter = new SseEmitter(60000L);
        LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> llmChain = buildLlm(history, chatConfig, chatPromptTemplate, emitter);

        Thread thread = new Thread(() -> {
            BaseLLMResult<ChatCompletionResult> run = llmChain.call(humanInput);
            emitter.complete();
            long end = System.currentTimeMillis();
            BigDecimal totalPrice = BigDecimal.valueOf(run.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
            LogAppMessageDO logAppMessageDO = LogAppMessageDO.builder()
                    .uid(IdUtil.getSnowflakeNextIdStr())
                    .message(request.getQuery())
                    .messageTokens(Math.toIntExact(run.getUsage().getPromptTokens()))
                    .messageUnitPrice(new BigDecimal("0.0200"))
                    .appConfig(JSON.toJSONString(appEntity))
                    .variables("{}")
                    .appUid(conversationDO.getAppUid())
                    .appStep("")
                    .appMode(AppModelEnum.CHAT.name())
                    .appConversationUid(conversationDO.getUid())
                    .answer(run.getText())
                    .answerTokens(Math.toIntExact(run.getUsage().getCompletionTokens()))
                    .answerUnitPrice(new BigDecimal("0.0200"))
                    .elapsed(end - start)
                    .totalPrice(totalPrice)
                    .currency("USD")
                    .fromScene("")
                    .status("SUCCESS")
                    .build();
            logAppMessageDO.setCreator(userId.toString());
            messageMapper.insert(logAppMessageDO);
            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), run.getUsage().getTotalTokens(), userId, logAppMessageDO.getUid());
            log.info("chat end , Response time: {} ms", end - start);
        });
        thread.start();
        return emitter;
    }

    private ChatMessageHistory preHistory(String conversationId, String appMode) {
        ChatMessageHistory history = new ChatMessageHistory();
        LambdaQueryWrapper<LogAppMessageDO> queryWrapper = Wrappers.lambdaQuery(LogAppMessageDO.class)
                .eq(LogAppMessageDO::getAppConversationUid, conversationId)
                .eq(LogAppMessageDO::getAppMode, appMode)
                .eq(LogAppMessageDO::getStatus, "SUCCESS")
                .orderByAsc(LogAppMessageDO::getId);
        List<LogAppMessageDO> appMessages = messageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(appMessages)) {
            return history;
        }
        for (LogAppMessageDO appMessage : appMessages) {
            history.addUserMessage(appMessage.getMessage());
            history.addAiMessage(appMessage.getAnswer());
        }
        return history;
    }

    private Map<String, String> getCleanInputs(Map<String, String> inputs, List<UserInputFromEntity> userInputForms) {
        Map<String, String> filteredInputs = new HashMap<>(userInputForms.size());
        if (CollectionUtils.isEmpty(userInputForms)) {
            return filteredInputs;
        }
        for (UserInputFromEntity userInputFrom : userInputForms) {
            String variable = userInputFrom.getVariable();
            if (inputs ==null || !inputs.containsKey(variable) || inputs.get(variable) == null) {
                if (BooleanUtils.isNotFalse(userInputFrom.getRequired())) {
                    throw new ServiceException(500, variable + " is required in input form!");
                } else {
                    filteredInputs.put(variable, userInputFrom.getDefaultValue());
                }
            }
            if (userInputFrom.getMaxLength() != null && inputs.get(variable).length() > userInputFrom.getMaxLength()) {
                throw new ServiceException(500, variable + " lengths must be less than " + userInputFrom.getMaxLength());
            }
            filteredInputs.put(variable, inputs.get(variable));
        }
        return filteredInputs;
    }


    private LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> buildLlm(ChatMessageHistory history,
                                                                                           ChatConfigEntity chatConfig,
                                                                                           ChatPromptTemplate chatPromptTemplate,
                                                                                           SseEmitter emitter) {
        ChatOpenAI chatOpenAi = new ChatOpenAI();
        ChatConfigConvert.INSTANCE.updateParams(chatConfig.getModelConfig().getCompletionParams(), chatOpenAi);
        chatOpenAi.getCallbackManager().addCallbackHandler(new StreamingSseCallBackHandler(emitter));
        ConversationBufferMemory memory = new ConversationBufferMemory();
        memory.setChatHistory(history);
        LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> llmChain = new LLMChain<>(chatOpenAi, chatPromptTemplate);
        llmChain.setMemory(memory);
        return llmChain;
    }
}
