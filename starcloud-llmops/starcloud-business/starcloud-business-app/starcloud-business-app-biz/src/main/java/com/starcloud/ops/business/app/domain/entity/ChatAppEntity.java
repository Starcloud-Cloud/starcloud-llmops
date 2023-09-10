package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.chat.ModelConfigEntity;
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
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.datasearch.GoogleSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.SearchEngineHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.domain.llm.PromptTemplateConfig;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.chat.momory.ConversationSummaryDbMessageMemory;
import com.starcloud.ops.business.app.util.SseResultUtil;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatQwen;
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
@JsonIgnoreType
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
    @JsonIgnore
    @JSONField(serialize = false)
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
    protected void doValidate(ChatRequestVO request) {

        //@todo 现在默认都挂载一个 数据集，具体是否能搜索靠后续向量搜索处理
        DatesetEntity datesetEntity = new DatesetEntity();
        datesetEntity.setDatasetUid(this.getUid());
        datesetEntity.setEnabled(true);
        //实时加载 数据集配置
        this.getChatConfig().setDatesetEntities(Arrays.asList(datesetEntity));


        getChatConfig().validate();
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Long getRunUserId(ChatRequestVO req) {

        if (!AppSceneEnum.inLoginUserIdScene(AppSceneEnum.valueOf(req.getScene()))) {
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
    protected void initHistory(ChatRequestVO request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageDOS) {

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
    protected void buildAppConversationLog(ChatRequestVO request, LogAppConversationCreateReqVO createRequest) {

        createRequest.setAppConfig(JSONUtil.toJsonStr(this.getChatConfig()));
    }


    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected JsonData doExecute(ChatRequestVO request) {

        this.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), request.getUserId());

        return executeChat(request, request.getUserId());
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doAsyncExecute(ChatRequestVO request) {
        JsonData jsonParams = this.doExecute(request);
    }

    /**
     * 模版方法：执行应用前置处理方法
     *
     * @param chatRequestVO 请求参数
     */
    @Override
    protected void beforeExecute(ChatRequestVO chatRequestVO) {

    }

    /**
     * 执行后执行
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void afterExecute(ChatRequestVO request, Throwable throwable) {

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
        this.getMessageMemory().initContentDocMemory();

        SseEmitter emitter = request.getSseEmitter();

        ChatConfigEntity chatConfig = this.getChatConfig();

        // 从表单配置中筛选输入变量，处理必填字段、默认值和选项值
        Map<String, Object> cleanInputs = getVariableItem(chatConfig.getVariable());

        //工具入参
        HandlerContext appContext = this.instanceHandlerContext(request);

        ChatPrePrompt chatPrePrompt = new ChatPrePrompt(chatConfig.getPrePrompt(), chatConfig.getPrePromptConfig());
        ContextPrompt contextPrompt = new ContextPrompt(chatConfig, request.getQuery(), this.getMessageMemory().getMessageContentDocMemory(), appContext);
        HistoryPrompt historyPrompt = new HistoryPrompt(this.getMessageMemory());

        ChatPrompt chatPrompt = new ChatPrompt(chatPrePrompt, contextPrompt, historyPrompt);

        int maxTokens = chatPrompt.calculateModelUseMaxToken(chatConfig.getModelConfig(), request.getQuery());
        //设置 memory 必要参数
        this.getMessageMemory().setSummaryMaxTokens(maxTokens);

        BaseVariable humanInput = BaseVariable.newString("input", request.getQuery());

        //直接把查询到到文档发送到前端
        if (contextPrompt.isEnable()) {
            //生成文档列表结果
            InteractiveInfo interactiveInfo = InteractiveInfo.buildDocs(contextPrompt.getSearchResult());
            SseResultUtil.builder().sseEmitter(request.getSseEmitter()).conversationUid(request.getConversationUid()).build().sendCallbackInteractive(interactiveInfo);
        }

        //千问调用
        if (ModelConfigEntity.ModelProviderEnum.QWEN.equals(this.getChatConfig().getModelConfig().getProvider())) {

            ChatPromptTemplate chatPromptTemplate = chatPrompt.buildChatPromptTemplate(false);

            LLMChain<GenerationResult> llmChain = buildQwenLlm(request, maxTokens, chatConfig, chatPromptTemplate, emitter);

            BaseLLMResult<GenerationResult> result = llmChain.call(Arrays.asList(humanInput));

            return JsonData.of(result);

        } else {

            //@todo 中间会有 function执行到逻辑, 调用方法 和 参数都要修改
            if ((chatConfig.getWebSearchConfig() != null && BooleanUtil.isTrue(chatConfig.getWebSearchConfig().getEnabled()))
                    || (CollectionUtil.isNotEmpty(chatConfig.getApiSkills())
                    || CollectionUtil.isNotEmpty(chatConfig.getAppWorkflowSkills())
                    || CollectionUtil.isNotEmpty(chatConfig.getHandlerSkills())
            )) {

                ChatPromptTemplate chatPromptTemplate = chatPrompt.buildChatPromptTemplate(this.getMessageMemory());

                AgentExecutor agentExecutor = buildLLmTools(request, chatConfig, chatPromptTemplate, emitter);

                //把上下文文档内容的 变量占位符传入
                AgentFinish agentAction = agentExecutor.call(Arrays.asList(humanInput));

                //扣费，记录 tool 调用日志


                //GPT 在做次总结

                //@todo  生成 message

                return JsonData.of(agentAction);

            } else {

                ChatPromptTemplate chatPromptTemplate = chatPrompt.buildChatPromptTemplate(false);

                LLMChain<ChatCompletionResult> llmChain = buildLlm(request, maxTokens, chatConfig, chatPromptTemplate, emitter);

                BaseLLMResult<ChatCompletionResult> result = llmChain.call(Arrays.asList(humanInput));

                return JsonData.of(result);
            }

        }

    }


    private void executeQwen() {

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

    /**
     * 千问 LLm
     *
     * @param request
     * @param maxTokens
     * @param chatConfig
     * @param chatPromptTemplate
     * @param emitter
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private LLMChain<GenerationResult> buildQwenLlm(ChatRequestVO request, int maxTokens,
                                                    ChatConfigEntity chatConfig,
                                                    ChatPromptTemplate chatPromptTemplate,
                                                    SseEmitter emitter) {
        ChatQwen chatQwen = new ChatQwen();

        chatQwen.setTopP(chatConfig.getModelConfig().getCompletionParams().getTemperature());
        chatQwen.setStream(true);

        chatQwen.getCallbackManager().addCallbackHandler(new MySseCallBackHandler(emitter, request));
        LLMChain<GenerationResult> llmChain = new LLMChain<>(chatQwen, chatPromptTemplate);

        llmChain.setMemory(this.getMessageMemory());
        return llmChain;
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
        chatOpenAI.setTemperature(0.7d);

        chatOpenAI.getCallbackManager().addCallbackHandler(new MySseCallBackHandler(emitter, request));

        List<BaseTool> tools = this.loadLLMTools(request, chatConfig, emitter);

        log.info("buildLLmTools: {} => {}", request.getAppUid(), Optional.ofNullable(tools).orElse(new ArrayList<>()).stream().map(BaseTool::getName).collect(Collectors.joining(", ")));

        //增加 统一的 promptTemplate
        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools, chatPromptTemplate);
        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(baseSingleActionAgent, tools, chatOpenAI.getCallbackManager());

        agentExecutor.setMemory(this.getMessageMemory());

        return agentExecutor;
    }


    /**
     * 初始化工具调用上下文
     *
     * @param request
     * @return
     */
    private HandlerContext instanceHandlerContext(ChatRequestVO request) {

        HandlerContext appContext = HandlerContext.createContext(request);
        appContext.setSseEmitter(request.getSseEmitter());

        return appContext;
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
        HandlerContext appContext = this.instanceHandlerContext(request);

        //web search
        if (searchConfigEntity != null && searchConfigEntity.getEnabled()) {

//            //爬取网页
//            WebSearch2DocHandler webSearch2Doc = new WebSearch2DocHandler();
//            String description = webSearch2Doc.getDescription() + PromptTemplateConfig.webSearchPrePrompt(searchConfigEntity);
//            webSearch2Doc.setDescription(description);
//            webSearch2Doc.setMessageContentDocMemory(this.getMessageMemory().getMessageContentDocMemory());
//            HandlerSkill handlerSkill = new HandlerSkill(webSearch2Doc);
//            loadTools.add(handlerSkill.createFunTool(appContext));
//
//            HandlerSkill searchEngine = HandlerSkill.of("SearchEngineHandler");
//            searchEngine.getHandler().setMessageContentDocMemory(this.getMessageMemory().getMessageContentDocMemory());
//            loadTools.add(searchEngine.createFunTool(appContext));

        }

        List<BaseTool> handlerFunTools = Optional.ofNullable(chatConfig.getHandlerSkills()).orElse(new ArrayList<>()).stream().filter(HandlerSkill::getEnabled).map(handlerSkill -> {

            if (handlerSkill.getHandler() != null) {
                //设置 memory
                BaseToolHandler baseToolHandler = handlerSkill.getHandler();

                baseToolHandler.setMessageContentDocMemory(this.getMessageMemory().getMessageContentDocMemory());

                return handlerSkill.createFunTool(appContext);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        loadTools.addAll(handlerFunTools);


        //API工具
        List<BaseTool> apiFunTools = Optional.ofNullable(chatConfig.getApiSkills()).orElse(new ArrayList<>()).stream().filter(ApiSkill::getEnabled).map(skillEntity -> {

            //设置 memory

            return skillEntity.createFunTool(appContext);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        loadTools.addAll(apiFunTools);

        //应用工具
        List<BaseTool> appFunTools = Optional.ofNullable(chatConfig.getAppWorkflowSkills()).orElse(new ArrayList<>()).stream().filter(AppWorkflowSkill::getEnabled).map(skillEntity -> {

            //设置 memory

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
    protected void doInsert() {
        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doUpdate() {
        getAppRepository().update(this);
    }


}
