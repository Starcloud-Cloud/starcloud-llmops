package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.chat.ChatRequest;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Slf4j
@Data
public class ImageAppEntity extends BaseAppEntity<ChatRequest, SseEmitter> {


    private static ChatService chatService = SpringUtil.getBean(ChatService.class);

    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private static ThreadPoolExecutor threadPoolExecutor = SpringUtil.getBean("CHAT_POOL_EXECUTOR");

    private static DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);


    private static AppRepository appRepository;


    private ChatMessageHistory chatMessageHistory;

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    /**
     * 校验
     */
    @Override
    protected void _validate() {
        getChatConfig().validate();
    }

    /**
     * 历史记录初始化
     */
    @Override
    protected void _initHistory(ChatRequest req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS) {

        //preHistory(request.getConversationUid(), AppModelEnum.CHAT.name());

        ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());

        this.chatMessageHistory = null;

    }


    @Override
    protected SseEmitter _execute(ChatRequest req) {


        Long userId = WebFrameworkUtils.getLoginUserId();
        SseEmitter emitter = new SseEmitter(60000L);
        Long tenantId = TenantContextHolder.getTenantId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        threadPoolExecutor.execute(() -> {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                TenantContextHolder.setTenantId(tenantId);
                executeChat(req, userId, emitter);
                emitter.complete();
            } catch (Exception e) {
                log.error("chat error:", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;

    }

    @Override
    protected ChatConfigEntity _parseConversationConfig(String conversationConfig) {
        ChatConfigEntity chatConfig = JSON.parseObject(conversationConfig, ChatConfigEntity.class);
        return chatConfig;
    }


    private void executeChat(ChatRequest request, Long userId, SseEmitter emitter) {
        benefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), userId);
        long start = System.currentTimeMillis();

        ChatConfigEntity chatConfig = this.getConversationConfig(request.getConversationUid());


        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable().getVariables());
        ChatMessageHistory history = this.chatMessageHistory;


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

        //@todo 中间会有 function执行到逻辑, 调用方法 和 参数都要修改
        LLMChain<ChatCompletionResult> llmChain = buildLlm(history, chatConfig, chatPromptTemplate, emitter);


        BaseLLMResult<ChatCompletionResult> result = llmChain.call(humanInput);
        long end = System.currentTimeMillis();
        BigDecimal totalPrice = BigDecimal.valueOf(result.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);


        String messageUid = IdUtil.getSnowflakeNextIdStr();
        this.createAppMessage((messageCreateReqVO) -> {

            messageCreateReqVO.setUid(messageUid);
            messageCreateReqVO.setMessage(request.getQuery());
            messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
            messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.0200"));

            messageCreateReqVO.setAppConfig(JSON.toJSONString(chatConfig));
            messageCreateReqVO.setVariables(JSON.toJSONString(chatConfig.getModelConfig()));
            messageCreateReqVO.setAppUid(request.getAppId());
            messageCreateReqVO.setAppStep("");
            messageCreateReqVO.setAppMode(AppModelEnum.CHAT.name());
            messageCreateReqVO.setAppConversationUid(request.getConversationUid());

            messageCreateReqVO.setAnswer(result.getText());
            messageCreateReqVO.setAnswerTokens(Math.toIntExact(result.getUsage().getCompletionTokens()));
            messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.0200"));
            messageCreateReqVO.setElapsed(end - start);
            messageCreateReqVO.setTotalPrice(totalPrice);
            messageCreateReqVO.setCurrency("USD");
            messageCreateReqVO.setFromScene("");
            messageCreateReqVO.setStatus("SUCCESS");

        });

        benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, messageUid);

        log.info("chat end , Response time: {} ms", end - start);
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

    private LLMChain<ChatCompletionResult> buildLlm(ChatMessageHistory history,
                                                    ChatConfigEntity chatConfig,
                                                    ChatPromptTemplate chatPromptTemplate,
                                                    SseEmitter emitter) {
        ChatOpenAI chatOpenAi = new ChatOpenAI();
        ChatConfigConvert.INSTANCE.updateParams(chatConfig.getModelConfig().getCompletionParams(), chatOpenAi);
        chatOpenAi.getCallbackManager().addCallbackHandler(new StreamingSseCallBackHandler(emitter));
        ConversationBufferMemory memory = new ConversationBufferMemory();
        memory.setChatHistory(history);
        LLMChain<ChatCompletionResult> llmChain = new LLMChain<>(chatOpenAi, chatPromptTemplate);
        llmChain.setMemory(memory);
        return llmChain;
    }


    /**
     * 新增应用
     */
    @Override
    protected void _insert() {

        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    protected void _update() {

        getAppRepository().update(this);
    }


}
