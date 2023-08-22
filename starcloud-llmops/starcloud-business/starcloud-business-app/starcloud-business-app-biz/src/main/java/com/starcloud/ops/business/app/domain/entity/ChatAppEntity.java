package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.chat.MySseCallBackHandler;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.ChatPrePrompt;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.ChatPrompt;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.ContextPrompt;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.HistoryPrompt;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.skill.ApiSkill;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.entity.skill.BaseSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.domain.llm.PromptTemplateConfig;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.chat.momory.ConversationSummaryDbMessageMemory;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @JsonIgnore
    @JSONField(serialize = false)
    private static ChatService chatService = SpringUtil.getBean(ChatService.class);

    @JsonIgnore
    @JSONField(serialize = false)
    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    @JsonIgnore
    @JSONField(serialize = false)
    private static DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);

    @JsonIgnore
    @JSONField(serialize = false)
    private ThreadWithContext threadExecutor = SpringUtil.getBean(ThreadWithContext.class);

    @JsonIgnore
    @JSONField(serialize = false)
    private static AppRepository appRepository;


    /**
     * 自定义memory 处理总结和tool历史问题。历史初始化时候新建
     */
    private ConversationSummaryDbMessageMemory messageMemory = new ConversationSummaryDbMessageMemory();

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    @JsonIgnore
    @JSONField(serialize = false)
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
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _validate(ChatRequestVO request) {

        //@todo 现在默认都挂载一个 数据集，具体是否能搜索靠后续向量搜索处理
        DatesetEntity datesetEntity = new DatesetEntity();
        datesetEntity.setDatasetUid(this.getUid());
        datesetEntity.setEnabled(true);
        //实时加载 数据集配置
        //this.getChatConfig().setDatesetEntities(Arrays.asList(datesetEntity));


        getChatConfig().validate();
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Long getRunUserId(ChatRequestVO req) {

        //如果是后台执行，肯定是当前应用创建者
        if (req.getScene().equals(AppSceneEnum.WEB_ADMIN.name())
                || req.getScene().equals(AppSceneEnum.WECOM_GROUP.name())
                || req.getScene().equals(AppSceneEnum.SHARE_WEB.name())
                || req.getScene().equals(AppSceneEnum.SHARE_JS.name())) {
            return Long.valueOf(this.getCreator());
        }

        return super.getRunUserId(req);
    }

    /**
     * 历史记录初始化
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _initHistory(ChatRequestVO request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageDOS) {

        //preHistory(request.getConversationUid(), AppModelEnum.CHAT.name());


        //所有场景都走最新发布的配置，不读取会话上的配置
        if (logAppConversation != null) {

//            //后台执行，不走历史配置，每次都是最新的配置
//            if (!(req.getScene().equals(AppSceneEnum.WEB_ADMIN.name())
//                    || req.getScene().equals(AppSceneEnum.WECOM_GROUP.name())
//                    || req.getScene().equals(AppSceneEnum.SHARE_WEB.name())
//                    || req.getScene().equals(AppSceneEnum.SHARE_JS.name()))) {
//                ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());
//                this.setChatConfig(chatConfig);
//            }
//
//            //分享场景，走最新发布内的配置
//            if (req.getScene().equals(AppSceneEnum.SHARE_WEB.name())) {
//
//                //获取最新发布的配置
//                req.getMediumUid();
//
//                ChatConfigEntity chatConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());
//                this.setChatConfig(chatConfig);
//            }
        }

        //有历史初始化一个 memory
        this.messageMemory = new ConversationSummaryDbMessageMemory(logAppMessageDOS);


    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _createAppConversationLog(ChatRequestVO request, LogAppConversationCreateReqVO createRequest) {

        createRequest.setAppConfig(JSONUtil.toJsonStr(this.getChatConfig()));
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ChatConfigEntity _parseConversationConfig(String conversationConfig) {
        ChatConfigEntity chatConfig = JSON.parseObject(conversationConfig, ChatConfigEntity.class);
        return chatConfig;
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected JsonData _execute(ChatRequestVO request) {

        this.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), request.getUserId());

        return executeChat(request, request.getUserId());
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _aexecute(ChatRequestVO request) {
        JsonData jsonParams = this._execute(request);
    }

    /**
     * 执行后执行
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _afterExecute(ChatRequestVO request, Throwable throwable) {

        SseEmitter sseEmitter = request.getSseEmitter();

        if (sseEmitter != null) {
            if (throwable != null) {
                sseEmitter.completeWithError(throwable);
            } else {
                sseEmitter.complete();
            }
        }
    }

    @JsonIgnore
    @JSONField(serialize = false)
    private JsonData executeChat(ChatRequestVO request, Long userId) {

        this.getMessageMemory().setChatAppEntity(this);
        this.getMessageMemory().setChatRequestVO(request);

        SseEmitter emitter = request.getSseEmitter();

        long start = System.currentTimeMillis();

        ChatConfigEntity chatConfig = this.getChatConfig();

        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable());

        ChatMessageHistory history = this.getMessageMemory().getChatHistory();

        ChatPrePrompt chatPrePrompt = new ChatPrePrompt(chatConfig.getPrePrompt(), chatConfig.getPrePromptConfig());
        ContextPrompt contextPrompt = new ContextPrompt(chatConfig.getDatesetEntities(), request.getQuery());
        HistoryPrompt historyPrompt = new HistoryPrompt(history != null && history.limitMessage(1).size() > 0);

        ChatPrompt chatPrompt = new ChatPrompt(chatPrePrompt, contextPrompt, historyPrompt);
        int maxTokens = chatPrompt.calculateModelUseMaxToken(chatConfig.getModelConfig(), request.getQuery());

        ChatPromptTemplate chatPromptTemplate = chatPrompt.buildChatPromptTemplate();

        BaseVariable humanInput = BaseVariable.newString("input", request.getQuery());

        log.info("chatPromptTemplate: {}, \n\n humanInput: {}", chatPromptTemplate, humanInput);

        //设置 memory 必要参数
        this.getMessageMemory().setSummaryMaxTokens(maxTokens);


        //@todo 中间会有 function执行到逻辑, 调用方法 和 参数都要修改
        if ((chatConfig.getWebSearchConfig() != null && BooleanUtil.isTrue(chatConfig.getWebSearchConfig().getEnabled()))
                || (CollectionUtil.isNotEmpty(chatConfig.getApiSkills()) || CollectionUtil.isNotEmpty(chatConfig.getAppWorkflowSkills()))) {

            AgentExecutor agentExecutor = buildLLmTools(request, chatConfig, chatPromptTemplate, emitter);

            AgentAction agentAction = agentExecutor.call(Arrays.asList(humanInput));

            log.info("agentExecutor run result: {}", agentAction);

            //扣费，记录 tool 调用日志


            //GPT 在做次总结

            //@todo  生成 message

            //记录返回日志

            //benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
            return JsonData.of(agentAction.getObservation());

        } else {

            //@todo 主动创建 messageUid
            //request.setMessageUid();

            LLMChain<ChatCompletionResult> llmChain = buildLlm(request, maxTokens, chatConfig, chatPromptTemplate, emitter);

            BaseLLMResult<ChatCompletionResult> result = llmChain.call(Arrays.asList(humanInput));

            llmChain.getMemory();

            //benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, message.getUid());
            return JsonData.of(result);
        }

    }

    @JsonIgnore
    @JSONField(serialize = false)
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

    @JsonIgnore
    @JSONField(serialize = false)
    private LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> buildLlm(ChatRequestVO request, int maxTokens,
                                                                                           ChatConfigEntity chatConfig,
                                                                                           ChatPromptTemplate chatPromptTemplate,
                                                                                           SseEmitter emitter) {
        ChatOpenAI chatOpenAi = new ChatOpenAI();

        //@todo 重新组装参数
        ChatConfigConvert.INSTANCE.updateParams(chatConfig.getModelConfig().getCompletionParams(), chatOpenAi);

        chatOpenAi.setStream(true);

        chatOpenAi.getCallbackManager().addCallbackHandler(new MySseCallBackHandler(emitter, request));
        LLMChain<com.theokanning.openai.completion.chat.ChatCompletionResult> llmChain = new LLMChain<>(chatOpenAi, chatPromptTemplate);

        llmChain.setMemory(this.getMessageMemory());
        return llmChain;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    private AgentExecutor buildLLmTools(ChatRequestVO request,
                                        ChatConfigEntity chatConfig,
                                        ChatPromptTemplate chatPromptTemplate,
                                        SseEmitter emitter) {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //@todo 重新组装参数
        ChatConfigConvert.INSTANCE.updateParams(chatConfig.getModelConfig().getCompletionParams(), chatOpenAI);

        chatOpenAI.setStream(false);
        chatOpenAI.setModel(ModelType.GPT_4.getName());
        chatOpenAI.setTemperature(0d);

        chatOpenAI.getCallbackManager().addCallbackHandler(new StreamingSseCallBackHandler(emitter, request.getConversationUid()));

        List<BaseTool> tools = this.loadLLMTools(request, chatConfig, emitter);

        //增加 统一的 promptTemplate
        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools, chatPromptTemplate);
        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(baseSingleActionAgent, tools, chatOpenAI.getCallbackManager());

        agentExecutor.setMemory(this.getMessageMemory());

        return agentExecutor;
    }

    /**
     * 技能转换 为 LLM 的 function 结构
     * 最后转换为 LLM中的 FunTool，通过回调再次调用"技能"的功能实现
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
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

        List<BaseTool> handlerFunTools = Optional.ofNullable(chatConfig.getHandlerSkills()).orElse(new ArrayList<>()).stream().filter(HandlerSkill::getEnabled).map(handlerSkill -> {

            if (handlerSkill.getHandler() != null) {
                return handlerSkill.createFunTool(appContext);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        loadTools.addAll(handlerFunTools);


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
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _insert() {
        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _update() {
        getAppRepository().update(this);
    }


}
