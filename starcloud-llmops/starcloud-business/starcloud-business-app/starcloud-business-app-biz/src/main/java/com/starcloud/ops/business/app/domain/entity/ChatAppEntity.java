package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.chat.MySseCallBackHandler;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.skill.ApiSkill;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.entity.skill.BaseSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.domain.llm.OpenAIToolFactory;
import com.starcloud.ops.business.app.domain.llm.PromptTemplateConfig;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.chat.momory.ConversationTokenDbBufferMemory;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
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
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 我的 chat 执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Slf4j
@Data
public class ChatAppEntity<Q, R> extends BaseAppEntity<ChatRequestVO, JsonData> {

    private static ChatService chatService = SpringUtil.getBean(ChatService.class);

    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private static DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);

    private ThreadWithContext threadExecutor = SpringUtil.getBean(ThreadWithContext.class);


    private static AppRepository appRepository;


    private ChatMessageHistory chatMessageHistory = new ChatMessageHistory();

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
    protected void _validate(ChatRequestVO req) {

        //@todo 现在默认都挂载一个 数据集，具体是否能搜索靠后续向量搜索处理
        DatesetEntity datesetEntity = new DatesetEntity();
        datesetEntity.setDatasetUid(this.getUid());
        datesetEntity.setEnabled(true);
        //实时加载 数据集配置
        //this.getChatConfig().setDatesetEntities(Arrays.asList(datesetEntity));


        getChatConfig().validate();
    }

    @Override
    protected Long getRunUserId(ChatRequestVO req) {

        //如果是后台执行，肯定是当前应用创建者
        if (req.getScene().equals(AppSceneEnum.WEB_ADMIN.name())
                || req.getScene().equals(AppSceneEnum.WECOM_GROUP.name())) {
            return Long.valueOf(this.getCreator());
        }

        return super.getRunUserId(req);
    }

    /**
     * 历史记录初始化
     */
    @Override
    protected void _initHistory(ChatRequestVO req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS) {

        //preHistory(request.getConversationUid(), AppModelEnum.CHAT.name());

        //@todo 根据不同场景自动处理 应用的会话配置信息
        if (logAppConversationDO != null) {

            //后台执行，不走历史配置，每次都是最新的配置
            if (!req.getScene().equals(AppSceneEnum.WEB_ADMIN.name())) {
                ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());
                this.setChatConfig(chatConfig);
            }

            //分享场景，走最新发布内的配置
            if (req.getScene().equals(AppSceneEnum.SHARE_WEB.name())) {

                //获取最新发布的配置
                req.getMediumUid();

                ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());
                this.setChatConfig(chatConfig);
            }
        }

        Optional.ofNullable(logAppMessageDOS).orElse(new ArrayList<>()).stream().forEach((logAppMessageDO -> {

            //@todo 判断状态，获取function记录？
            chatMessageHistory.addUserMessage(logAppMessageDO.getMessage());
            chatMessageHistory.addAiMessage(logAppMessageDO.getAnswer());

        }));

    }

    @Override
    protected void _createAppConversationLog(ChatRequestVO req, LogAppConversationCreateReqVO logAppConversationCreateReqVO) {

        logAppConversationCreateReqVO.setAppConfig(JSONUtil.toJsonStr(this.getChatConfig()));
    }

    @Override
    protected ChatConfigEntity _parseConversationConfig(String conversationConfig) {
        ChatConfigEntity chatConfig = JSON.parseObject(conversationConfig, ChatConfigEntity.class);
        return chatConfig;
    }

    @Override
    protected JsonData _execute(ChatRequestVO req) {

        this.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), req.getUserId());

        return executeChat(req, req.getUserId());
    }

    @Override
    protected void _aexecute(ChatRequestVO req) {
        JsonData jsonParams = this._execute(req);
    }

    /**
     * 执行后执行
     */
    @Override
    protected void _afterExecute(ChatRequestVO req, Throwable t) {

        SseEmitter sseEmitter = req.getSseEmitter();

        if (sseEmitter != null) {
            if (t != null) {
                sseEmitter.completeWithError(t);
            } else {
                sseEmitter.complete();
            }
        }
    }


    private JsonData executeChat(ChatRequestVO request, Long userId) {

        SseEmitter emitter = request.getSseEmitter();

        long start = System.currentTimeMillis();

        ChatConfigEntity chatConfig = this.getChatConfig();

        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable());
        ChatMessageHistory history = this.chatMessageHistory;


        StringJoiner messageTemp = new StringJoiner(StringUtils.LF);

        // 配置项
        if (StringUtils.isNotBlank(chatConfig.getPrePrompt())) {
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
        if (history != null && history.limitMessage(1).size() > 0) {
            messageTemp.add(PromptTempletEnum.HISTORY_TEMP.getTemp());
        }

        humanInput.put(PromptTempletEnum.INPUT_TEMP.getKey(), request.getQuery());
        messageTemp.add(PromptTempletEnum.INPUT_TEMP.getTemp());

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Collections.singletonList(
                        HumanMessagePromptTemplate.fromTemplate(messageTemp.toString())
                )
        );

        int maxToken = calculateMaxTokens(chatConfig.getPrePrompt(),
                String.valueOf(humanInput.get(PromptTempletEnum.DATASET_CONTEXT.getKey())),
                request.getQuery(), chatConfig.getModelConfig().getCompletionParams());

        //@todo 中间会有 function执行到逻辑, 调用方法 和 参数都要修改
        if ((chatConfig.getWebSearchConfig() != null && chatConfig.getWebSearchConfig().getEnabled())
                || (CollectionUtil.isNotEmpty(chatConfig.getApiSkills()) || CollectionUtil.isNotEmpty(chatConfig.getAppWorkflowSkills()))) {

            AgentExecutor agentExecutor = buildLLmTools(request, history, chatConfig, chatPromptTemplate, emitter);

            String response = agentExecutor.run(request.getQuery());
            Map result = JSONUtil.toBean(response, Map.class);

            log.info("result: {}", result);

            //GPT 在做次总结

            //@todo  生成 message

            //benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
            return JsonData.of(result.getOrDefault("output", ""));

        } else {

            //@todo 主动创建 messageUid
            //request.setMessageUid();

            LLMChain<ChatCompletionResult> llmChain = buildLlm(request, maxToken, chatConfig, chatPromptTemplate, emitter);

            BaseLLMResult<ChatCompletionResult> result = llmChain.call(humanInput);

            long end = System.currentTimeMillis();
            BigDecimal totalPrice = BigDecimal.valueOf(result.getUsage().getTotalTokens()).multiply(new BigDecimal("0.0200")).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);

            LogAppMessageCreateReqVO message = this.createAppMessage((messageCreateReqVO) -> {

                messageCreateReqVO.setAppConversationUid(request.getConversationUid());


                messageCreateReqVO.setMessage(request.getQuery());
                messageCreateReqVO.setMessageTokens(Math.toIntExact(result.getUsage().getPromptTokens()));
                messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.0200"));

                messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(chatConfig));
                messageCreateReqVO.setVariables(JSONUtil.toJsonStr(chatConfig.getModelConfig()));
                messageCreateReqVO.setAppUid(request.getAppUid());
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
                messageCreateReqVO.setCreator(String.valueOf(request.getUserId()));

            });

            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
            return JsonData.of(result);
        }

    }

    /**
     * 计算除去描述，数据集，当前问题后剩余token数
     * histroy最大剩余数
     *
     * @param prePrompt
     * @param dataSetStr
     * @param currQuery
     * @param openaiCompletionParams 使用模型配置
     * @return
     */
    public int calculateMaxTokens(String prePrompt, String dataSetStr, String currQuery, OpenaiCompletionParams openaiCompletionParams) {
        Optional<ModelType> optionalModel = ModelType.fromName(openaiCompletionParams.getModel());
        ModelType modelType = ModelType.GPT_3_5_TURBO;
        if (optionalModel.isPresent()) {
            modelType = optionalModel.get();
        }
        int maxTokens = modelType.getMaxContextLength();
        if (StringUtils.isNotBlank(prePrompt)) {
            maxTokens -= TokenUtils.intTokens(modelType, prePrompt);
        }
        if (StringUtils.isNotBlank(dataSetStr)) {
            maxTokens -= TokenUtils.intTokens(modelType, dataSetStr + PromptTempletEnum.DATASET_CONTEXT.getTemp());
        }

        if (openaiCompletionParams.getMaxTokens() != null && openaiCompletionParams.getMaxTokens() > 0) {
            // 临界值时 总结会多最后一次对话的回复 预先扣除token
            maxTokens -= openaiCompletionParams.getMaxTokens() * 2;
        } else {
            maxTokens -= 500 * 2;
        }

        maxTokens -= TokenUtils.intTokens(modelType, currQuery);
        return maxTokens;
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

    private LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> buildLlm(ChatRequestVO request, int maxTokens,
                                                                                           ChatConfigEntity chatConfig,
                                                                                           ChatPromptTemplate chatPromptTemplate,
                                                                                           SseEmitter emitter) {
        ChatOpenAI chatOpenAi = new ChatOpenAI();

        //@todo 重新组装参数
        ChatConfigConvert.INSTANCE.updateParams(chatConfig.getModelConfig().getCompletionParams(), chatOpenAi);

        chatOpenAi.setStream(true);

        chatOpenAi.getCallbackManager().addCallbackHandler(new MySseCallBackHandler(emitter, request));
//        ConversationBufferMemory memory = new ConversationBufferMemory();
        ConversationTokenDbBufferMemory conversationTokenDbBufferMemory = new ConversationTokenDbBufferMemory(maxTokens, request.getConversationUid(), chatConfig.getModelConfig().getCompletionParams().getModel());

//        memory.setChatHistory(history);
        LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> llmChain = new LLMChain<>(chatOpenAi, chatPromptTemplate);
        llmChain.setMemory(conversationTokenDbBufferMemory);
        return llmChain;
    }

    private AgentExecutor buildLLmTools(ChatRequestVO request, ChatMessageHistory history,
                                        ChatConfigEntity chatConfig,
                                        ChatPromptTemplate chatPromptTemplate,
                                        SseEmitter emitter) {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setStream(false);
        //gpt-3.5-turbo-0613, gpt-4-0613
        chatOpenAI.setModel("gpt-4-0613");
        chatOpenAI.getCallbackManager().addCallbackHandler(new StreamingSseCallBackHandler(emitter, request.getConversationUid()));
        ConversationBufferMemory memory = new ConversationBufferMemory();
        if (history != null) {
            memory.setChatHistory(history);
        }

        List<BaseTool> tools = this.loadLLMTools(request, chatConfig, emitter);

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);
        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(tools, chatOpenAI, baseSingleActionAgent, baseSingleActionAgent.getCallbackManager());

        agentExecutor.setMemory(memory);

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());

        return agentExecutor;
    }

    /**
     * 技能转换 为 LLM 的 function 结构
     * 最后转换为 LLM中的 FunTool，通过回调再次调用"技能"的功能实现
     *
     * @return
     */
    private List<BaseTool> loadLLMTools(ChatRequestVO request, ChatConfigEntity chatConfig, SseEmitter emitter) {

        List<BaseSkillEntity> skillEntities = new ArrayList<>();

        List<BaseTool> loadTools = new ArrayList<>();

        WebSearchConfigEntity searchConfigEntity = chatConfig.getWebSearchConfig();

        //工具入参
        HandlerContext appContext = HandlerContext.createContext(request.getAppUid(), request.getConversationUid(), request.getUserId());
        appContext.setSseEmitter(emitter);

        //web search
        if (searchConfigEntity != null && searchConfigEntity.getEnabled()) {

            WebSearch2DocHandler webSearch2Doc = new WebSearch2DocHandler();
            String description = webSearch2Doc.getDescription() + PromptTemplateConfig.webSearchPrePrompt(searchConfigEntity);
            webSearch2Doc.setDescription(description);

            HandlerSkill handlerSkill = new HandlerSkill(webSearch2Doc);

            loadTools.add(handlerSkill.createFunTool(appContext));
        }

        //API工具
        List<BaseTool> apiFunTools = Optional.ofNullable(chatConfig.getApiSkills()).orElse(new ArrayList<>()).stream().filter(ApiSkill::getEnabled).map(skillEntity -> {
            return skillEntity.createFunTool(appContext);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        loadTools.addAll(apiFunTools);

        //应用工具
        List<BaseTool> appFunTools = Optional.ofNullable(chatConfig.getAppWorkflowSkills()).orElse(new ArrayList<>()).stream().filter(AppWorkflowSkill::getEnabled).map(skillEntity -> {
            return skillEntity.createFunTool(appContext);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        loadTools.addAll(appFunTools);

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
