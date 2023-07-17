package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.chat.ChatRequest;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.ApiSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.SkillEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
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
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
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
public class ChatAppEntity extends BaseAppEntity<ChatRequest, SseEmitter> {


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

        if (logAppConversationDO != null) {
            ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());
            this.setChatConfig(chatConfig);
        }

        ChatMessageHistory history = new ChatMessageHistory();

        Optional.ofNullable(logAppMessageDOS).orElse(new ArrayList<>()).stream().forEach((logAppMessageDO -> {

            //@todo 判断状态，获取function记录？
            history.addUserMessage(logAppMessageDO.getMessage());
            history.addAiMessage(logAppMessageDO.getAnswer());

        }));

        this.chatMessageHistory = history;

    }


    @Override
    protected SseEmitter _execute(ChatRequest req) {

        Long userId = WebFrameworkUtils.getLoginUserId();

        benefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), userId);

        SseEmitter emitter = new SseEmitter(60000L);
        Long tenantId = TenantContextHolder.getTenantId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            TenantContextHolder.setTenantId(tenantId);
            executeChat(req, userId, emitter);
            emitter.complete();
        } catch (Exception e) {
            log.error("chat error:", e);
            emitter.completeWithError(e);
        }

//        threadPoolExecutor.execute(() -> {
//            try {
//                RequestContextHolder.setRequestAttributes(requestAttributes);
//                TenantContextHolder.setTenantId(tenantId);
//                executeChat(req, userId, emitter);
//                emitter.complete();
//            } catch (Exception e) {
//                log.error("chat error:", e);
//                emitter.completeWithError(e);
//            }
//        });

        return emitter;

    }

    @Override
    protected ChatConfigEntity _parseConversationConfig(String conversationConfig) {
        ChatConfigEntity chatConfig = JSON.parseObject(conversationConfig, ChatConfigEntity.class);
        return chatConfig;
    }


    private void executeChat(ChatRequest request, Long userId, SseEmitter emitter) {

        long start = System.currentTimeMillis();

        ChatConfigEntity chatConfig = this.getChatConfig();

        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable());
        ChatMessageHistory history = this.chatMessageHistory;


        StringJoiner messageTemp = new StringJoiner(StringUtils.LF);

        // 配置项
        if (cleanInputs.size() > 0) {
            messageTemp.add(chatConfig.getPrePrompt());
        }

        // 数据集
        List<String> datasetUid = Optional.ofNullable(chatConfig.getDatesetEntities()).orElse(new ArrayList<>())
                .stream().filter(DatesetEntity::getEnabled).map(DatesetEntity::getDatasetUid).collect(Collectors.toList());

        List<String> context = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(datasetUid)) {
            //@todo 需要 block 对象
            SimilarQueryRequest similarQueryRequest = new SimilarQueryRequest();
            similarQueryRequest.setQuery(request.getQuery());
            similarQueryRequest.setK(2L);
            similarQueryRequest.setDatasetUid(datasetUid);
            context = documentSegmentsService.similarQuery(similarQueryRequest);
        }

        Map<String, Object> humanInput = new HashMap<>(cleanInputs);
        if (!CollectionUtils.isEmpty(context)) {
            messageTemp.add(PromptTempletEnum.DATASET_CONTEXT.getTemp());

            //@todo 上下文走模版配置
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
                        HumanMessagePromptTemplate.fromTemplate(messageTemp.toString())
                )
        );


        BaseLLMResult<ChatCompletionResult> result;

        //@todo 中间会有 function执行到逻辑, 调用方法 和 参数都要修改
        if ((chatConfig.getWebSearchConfig() != null && chatConfig.getWebSearchConfig().getEnabled()) || CollectionUtil.isNotEmpty(chatConfig.getSkills())) {

            buildLLmTools(history, chatConfig, chatPromptTemplate, emitter);
            result = null;

        } else {

            LLMChain<ChatCompletionResult> llmChain = buildLlm(history, chatConfig, chatPromptTemplate, emitter);
            result = llmChain.call(humanInput);
        }


        long end = System.currentTimeMillis();
        BigDecimal totalPrice = BigDecimal.valueOf(result.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);


        String messageUid = IdUtil.fastSimpleUUID();
        this.createAppMessage((messageCreateReqVO) -> {

            messageCreateReqVO.setAppConversationUid(request.getConversationUid());

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
            messageCreateReqVO.setFromScene(request.getScene());
            messageCreateReqVO.setStatus("SUCCESS");

        });

        benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, messageUid);

        log.info("chat end , Response time: {} ms", end - start);
    }


    private Map<String, Object> getVariableItem(VariableEntity variable) {

        if (variable == null) {
            return MapUtil.newHashMap();
        }
        List<VariableItemEntity> variables = variable.getVariables();
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

    private void buildLLmTools(ChatMessageHistory history,
                               ChatConfigEntity chatConfig,
                               ChatPromptTemplate chatPromptTemplate,
                               SseEmitter emitter) {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setModel("gpt-4-0613"); //gpt-3.5-turbo-0613, gpt-4-0613

        List<SkillEntity> skillEntities = chatConfig.getSkills();
        List<BaseTool> tools = this.loadLLMTools(chatConfig, skillEntities);

        chatPromptTemplate.formatPrompt();

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(tools, chatOpenAI, baseSingleActionAgent, baseSingleActionAgent.getCallbackManager());

        agentExecutor.run("Who is Leo DiCaprio's girlfriend Or ex-girlfriend? What is her current age raised to the 0.43 power?");

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());


    }

    /**
     * 技能转换 为 LLM 的 function 结构
     * 最后转换为 LLM中的 FunTool，通过回调再次调用"技能"的功能实现
     *
     * @return
     */
    private List<BaseTool> loadLLMTools(ChatConfigEntity chatConfig, List<SkillEntity> skillEntities) {

        List<BaseTool> loadTools = new ArrayList<>();

        WebSearchConfigEntity searchConfigEntity = chatConfig.getWebSearchConfig();

        //web search
        if (searchConfigEntity != null && searchConfigEntity.getEnabled()) {
            SerpAPITool serpAPITool = new SerpAPITool();
            if (StrUtil.isNotBlank(searchConfigEntity.getWebScope())) {
                String newDesc = serpAPITool.getDescription();
                StrUtil.replace(newDesc + ".Note do not call the tool if you encounter the following web domains <<<{WebScope}>>>", "{WebScope}", searchConfigEntity.getWebScope());
                serpAPITool.setDescription(newDesc);
            }

            loadTools.add(serpAPITool);
        }

        //load skill
        List<BaseTool> funTools = Optional.ofNullable(skillEntities).orElse(new ArrayList<>()).stream().map((skillEntity -> {

            String name = skillEntity.getName();
            String desc = skillEntity.getDesc();

            skillEntity.getInputCls();

            if (skillEntity instanceof ApiSkillEntity) {

                FunTool funTool = new FunTool(name, desc, skillEntity.getInputSchemas(), (input) -> {
                    log.info("funTool: {} {}", name, input);

                    skillEntity.execute(input);

                    return null;
                });

                return funTool;
            }

            if (skillEntity instanceof AppWorkflowSkillEntity) {
                FunTool funTool = new FunTool(name, desc, skillEntity.getInputSchemas(), (input) -> {
                    log.info("funTool: {} {}", name, input);

                    skillEntity.execute(input);

                    return null;
                });

                return funTool;
            }

            return null;

        })).filter(Objects::nonNull).collect(Collectors.toList());

        loadTools.addAll(funTools);

        return loadTools;
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
