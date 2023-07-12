package com.starcloud.ops.business.app.service.chat.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.chat.ChatRequest;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.config.UserInputFromEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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
    private LogAppMessageService logAppMessageDO;

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private UserBenefitsService benefitsService;

    @Resource(name = "CHAT_POOL_EXECUTOR")
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public List<String> chatSuggestion(String conversationUid) {
        List<String> suggestion = new ArrayList<>();
        String resultText = StringUtils.EMPTY;
        try {
            Long userId = WebFrameworkUtils.getLoginUserId();
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), userId);
            ChatMessageHistory history = preHistory(conversationUid, AppModelEnum.CHAT.name());
            String messageTemp = PromptTempletEnum.SUGGESTED_QUESTIONS.getTemp();
            ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Collections.singletonList(
                            HumanMessagePromptTemplate.fromTemplate(messageTemp)
                    )
            );
            ChatOpenAI chatOpenAi = new ChatOpenAI();
            ConversationBufferMemory memory = new ConversationBufferMemory();
            memory.setChatHistory(history);
            List<BaseVariable> variables = memory.loadMemoryVariables();
            PromptValue promptValue = chatPromptTemplate.formatPrompt(variables);
            BaseLLMResult<ChatCompletionResult> result = chatOpenAi.generatePrompt(Collections.singletonList(promptValue));
            resultText = result.getText();
            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, conversationUid);
            return JSON.parseArray(resultText, String.class);
        } catch (Exception e) {
            log.error("suggestion error, openai result: {}.", resultText, e);
            return suggestion;
        }
    }

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
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable().getVariables());
        ChatMessageHistory history = preHistory(conversationDO.getUid(), AppModelEnum.CHAT.name());
        StringJoiner messageTemp = new StringJoiner(StringUtils.LF);

        // 配置项
        if (cleanInputs.size() > 0) {
            messageTemp.add(chatConfig.getPrePrompt());
        }

        // 数据集

        List<String> datasetUid = Optional.ofNullable(chatConfig.getDatesetEntities()).orElse(new ArrayList<>())
                .stream().filter(DatesetEntity::getEnabled).map(DatesetEntity::getDatasetUid).collect(Collectors.toList());

        SimilarQueryRequest similarQueryRequest = new SimilarQueryRequest();
        similarQueryRequest.setQuery(request.getQuery());
        similarQueryRequest.setK(2L);
        similarQueryRequest.setDatasetUid(datasetUid);
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
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        threadPoolExecutor.execute(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            BaseLLMResult<ChatCompletionResult> result = llmChain.call(humanInput);
            long end = System.currentTimeMillis();
            BigDecimal totalPrice = BigDecimal.valueOf(result.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);

            LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();
            messageCreateReqVO.setUid(IdUtil.getSnowflakeNextIdStr());
            messageCreateReqVO.setMessage(request.getQuery());
            messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
            messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.0200"));
            messageCreateReqVO.setAppConfig(JSON.toJSONString(appEntity));
            messageCreateReqVO.setVariables(JSON.toJSONString(chatConfig.getVariable()));
            messageCreateReqVO.setAppUid(conversationDO.getAppUid());
            messageCreateReqVO.setAppStep("");
            messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
            messageCreateReqVO.setAppConversationUid(conversationDO.getUid());
            messageCreateReqVO.setAnswer(result.getText());
            messageCreateReqVO.setAnswerTokens(Math.toIntExact(result.getUsage().getCompletionTokens()));
            messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.0200"));
            messageCreateReqVO.setElapsed(end - start);
            messageCreateReqVO.setTotalPrice(totalPrice);
            messageCreateReqVO.setCurrency("USD");
            messageCreateReqVO.setFromScene("");
            messageCreateReqVO.setStatus("SUCCESS");
            logAppMessageDO.createAppMessage(messageCreateReqVO);
            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, messageCreateReqVO.getUid());
            emitter.complete();
            log.info("chat end , Response time: {} ms", end - start);
        });
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


    private Map<String, Object> getVariableItem(List<VariableItemEntity> variables) {
        Map<String, Object> filteredInputs = new HashMap<>();
        if (CollectionUtils.isEmpty(variables)) {
            return filteredInputs;
        }
        for (VariableItemEntity item : variables) {
            Object value = item.getValue();
            if (value != null) {
                filteredInputs.put(item.getField(), value);
            } else {
                filteredInputs.put(item.getField(), item.getDefaultValue());
            }

        }
        return filteredInputs;
    }

    private Map<String, String> getCleanInputs(Map<String, String> inputs, List<UserInputFromEntity> userInputForms) {
        Map<String, String> filteredInputs = new HashMap<>();
        if (CollectionUtils.isEmpty(userInputForms)) {
            return filteredInputs;
        }
        for (UserInputFromEntity userInputFrom : userInputForms) {
            String variable = userInputFrom.getVariable();
            if (inputs == null || !inputs.containsKey(variable) || inputs.get(variable) == null) {
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
