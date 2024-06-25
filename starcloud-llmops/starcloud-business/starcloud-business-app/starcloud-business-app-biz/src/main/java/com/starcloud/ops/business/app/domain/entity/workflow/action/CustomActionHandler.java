package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class CustomActionHandler extends BaseActionHandler {

    /**
     * 字典服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppDictionaryService appDictionaryService = SpringUtil.getBean(AppDictionaryService.class);

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "CustomActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }

    /**
     * 具体handler的出参定义
     *
     * @return
     */
    @Override
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {

        //优先返回 素材类型的结构
//        String refers = (String) params.get(CreativeConstants.MATERIAL_TYPE);
//        if (StrUtil.isNotBlank(refers)) {
//            //获取参考素材的结构
//            return JsonSchemaUtils.generateJsonSchema(MaterialTypeEnum.of(refers).getAClass());
//        }

        return super.getOutVariableJsonSchema(workflowStepWrapper);
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param context
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeSchemeGenerateModeEnum.AI_PARODY.name()));

        // 随机模式
        if (CreativeSchemeGenerateModeEnum.RANDOM.name().equals(generateMode)) {
            return this.doRandomExecute(context, params);
        }

        // AI仿写模式
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(generateMode)) {
            return this.doAiParodyExecute(context, params);
        }

        // AI自定义模式
        if (CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(generateMode)) {
            return this.doAiCustomExecute(context, params);
        }

        // 不支持的生成模式
        throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 不支持的生成模式: {}", generateMode);
    }

    /**
     * 随机获取模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doRandomExecute(AppContext context, Map<String, Object> params) {

        log.info("内容生成步骤【开始执行】: 执行步骤: {}, 生成模式: {}, 应用UID: {}",
                context.getStepId(), CreativeSchemeGenerateModeEnum.RANDOM.name(), context.getUid());

        // 获取到参考文案
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS, "[]"));
        List<AbstractCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractCreativeMaterialDTO.class);
        if (CollectionUtil.isEmpty(referList)) {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 生成模式为【{}】时，参考内容不能为空！", context.getStepId(), CreativeSchemeGenerateModeEnum.RANDOM.getLabel());
        }

        // 随机获取一条参考文案，作为生成结果
        AbstractCreativeMaterialDTO reference = referList.get(RandomUtil.randomInt(referList.size()));

        // 计算价格相关结果
        ModelTypeEnum llmModel = ModelTypeEnum.GPT_3_5_TURBO;
        String message = CreativeSchemeGenerateModeEnum.RANDOM.name();
        String answer = reference.generateContent();
        long messageTokens = message.length();
        long answerTokens = answer.length();
        long totalTokens = messageTokens + answerTokens;
        BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(llmModel, true);
        BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(llmModel, false);
        BigDecimal messagePrice = TokenCalculator.getTextPrice(messageTokens, llmModel, true);
        BigDecimal answerPrice = TokenCalculator.getTextPrice(answerTokens, llmModel, false);
        BigDecimal totalPrice = messagePrice.add(answerPrice);
        int costPoints = CostPointUtils.obtainMagicBeanCostPoint(llmModel.getName(), totalTokens);

        // 返回响应结果
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setType(AppStepResponseTypeEnum.TEXT.name());
        actionResponse.setIsShow(Boolean.TRUE);
        actionResponse.setAiModel(llmModel.getName());
        actionResponse.setMessage(message);
        actionResponse.setAnswer(answer);
        actionResponse.setOutput(JsonData.of(answer));
        actionResponse.setMessageTokens(messageTokens);
        actionResponse.setMessageUnitPrice(messageUnitPrice);
        actionResponse.setAnswerTokens(answerTokens);
        actionResponse.setAnswerUnitPrice(answerUnitPrice);
        actionResponse.setTotalTokens(totalTokens);
        actionResponse.setTotalPrice(totalPrice);
        actionResponse.setCostPoints(costPoints);
        actionResponse.setStepConfig(params);

        log.info("内容生成步骤【执行成功】: 执行步骤: {}, 生成模式: {}, 应用UID: {}, 生成结果: \n{}",
                context.getStepId(),
                CreativeSchemeGenerateModeEnum.RANDOM.name(),
                context.getUid(),
                JsonUtils.toJsonPrettyString(actionResponse));

        return actionResponse;
    }

    /**
     * AI 仿写模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doAiParodyExecute(AppContext context, Map<String, Object> params) {
        String generateMode = CreativeSchemeGenerateModeEnum.AI_PARODY.name();
        String stepId = context.getStepId();
        log.info("内容生成步骤【开始执行】: 执行步骤: {}, 生成模式: {}, 应用UID: {}", stepId, generateMode, context.getUid());

        // 获取到参考内容
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS, "[]"));
        List<AbstractCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractCreativeMaterialDTO.class);
        if (CollectionUtil.isEmpty(referList)) {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 生成模式为【{}】时，参考内容不能为空！", stepId, generateMode);
        }

        // 需要交给 ChatGPT 的参考内容数量
        Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.REFERS_COUNT, "3")));

        // 处理参考内容
        List<AbstractCreativeMaterialDTO> handlerReferList = handlerReferList(referList, refersCount);
        AbstractCreativeMaterialDTO reference = handlerReferList.get(0);

        context.putVariable(CreativeConstants.REFERS, JsonUtils.toJsonPrettyString(handlerReferList));
        context.putVariable(CreativeConstants.REQUIREMENT, params.getOrDefault(CreativeConstants.REQUIREMENT, ""));
        context.putVariable(CreativeConstants.SYS_PROMPT, sysPrompt());

        // 重新获取上下文处理参数，因为参考内容已经被处理了，需要重新获取
        params = context.getContextVariablesValues();

        /*
         * 约定：prompt 为总的 prompt，包含了 AI仿写 和 AI自定义 的 prompt. 中间用 ---------- 分割
         * AI仿写为第一个 prompt
         * AI自定义为第二个 prompt
         */
        // 获取到 prompt
        String prompt = this.getPrompt(context, params, false);

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getAiModel(context)).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(context.getN()).orElse(1);
        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault("MAX_TOKENS", "1000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault("TEMPERATURE", "0.7")));

        log.info("内容生成步骤【调用大模型】: 执行步骤: {}, 生成模式: {}, 应用UID: {}, 执行参数: \n{}", generateMode, stepId, context.getUid(),
                JsonUtils.toJsonPrettyString(params));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(context.getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(context, handlerRequest);
        //本身输出已经走Sse了，不需要在发送一次完整的结果
        actionResponse.setIsSendSseAll(false);

        log.info("内容生成步骤【执行成功】: 生成模式: {}, 步骤ID: {}, 应用UID: {}, 生成结果: \n{}", generateMode, stepId, context.getStepId(),
                JsonUtils.toJsonPrettyString(actionResponse));

        return actionResponse;
    }

    /**
     * AI 仿写模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doAiCustomExecute(AppContext context, Map<String, Object> params) {
        String generateMode = CreativeSchemeGenerateModeEnum.AI_CUSTOM.name();
        String stepId = context.getStepId();
        log.info("内容生成步骤【开始执行】: 执行步骤: {}, 生成模式: {}, 应用UID: {}", stepId, generateMode, context.getUid());

        context.putVariable(CreativeConstants.REQUIREMENT, params.getOrDefault(CreativeConstants.REQUIREMENT, ""));
        context.putVariable(CreativeConstants.SYS_PROMPT, sysPrompt());
        params = context.getContextVariablesValues();

        /*
         * 约定：prompt 为总的 prompt，包含了 AI仿写 和 AI自定义 的 prompt. 中间用 ---------- 分割
         * AI仿写为第一个 prompt
         * AI自定义为第二个 prompt
         */
        // 获取到 prompt
        String prompt = this.getPrompt(context, params, true);

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getAiModel(context)).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(context.getN()).orElse(1);
        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault("MAX_TOKENS", "1000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault("TEMPERATURE", "0.7")));

        log.info("内容生成步骤【调用大模型】: 执行步骤: {}, 生成模式: {}, 应用UID: {}, 执行参数: \n{}", generateMode, stepId, context.getUid(),
                JsonUtils.toJsonPrettyString(params));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(context.getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(context, handlerRequest);
        //本身输出已经走Sse了，不需要在发送一次完整的结果
        actionResponse.setIsSendSseAll(false);

        log.info("内容生成步骤【执行成功】: 生成模式: {}, 步骤ID: {}, 应用UID: {}, 生成结果: \n{}", generateMode, stepId, context.getStepId(),
                JsonUtils.toJsonPrettyString(actionResponse));
        return actionResponse;
    }

    /**
     * 执行AI生成
     *
     * @param handlerRequest 请求
     * @return 结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doGenerateExecute(AppContext context, OpenAIChatHandler.Request handlerRequest) {
        // 构建请求上下文
        HandlerContext<OpenAIChatHandler.Request> handlerContext = HandlerContext.createContext(
                this.getAppUid(context),
                context.getConversationUid(),
                context.getUserId(),
                context.getEndUserId(),
                context.getScene(),
                handlerRequest
        );
        // 构建OpenAI处理器
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);
        // 执行OpenAI处理器
        HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
        // 转换并且返回响应结果
        return convert(context, handlerResponse);
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(AppContext context, HandlerResponse handlerResponse) {
        // 计算权益点数
        Long tokens = handlerResponse.getMessageTokens() + handlerResponse.getAnswerTokens();
        String llmModel = Optional.ofNullable(this.getAiModel(context)).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName());
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(llmModel, tokens);

        // 构建响应结果
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());
        actionResponse.setAnswer(handlerResponse.getAnswer());
        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        actionResponse.setAiModel(llmModel);
        try {
            // 解析输出内容
            JsonData output = this.parseOutput(context, handlerResponse);
            // 设置输出内容
            handlerResponse.setOutput(output);
            actionResponse.setCostPoints(handlerResponse.getSuccess() ? costPoints : 0);
        } catch (Exception exception) {
            actionResponse.setSuccess(Boolean.FALSE);
            if (exception instanceof ServiceException) {
                ServiceException serviceException = (ServiceException) exception;
                actionResponse.setErrorCode(String.valueOf(serviceException.getCode()));
            } else {
                actionResponse.setErrorCode(ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE.getCode().toString());
            }
            actionResponse.setErrorMsg("【{}】步骤执行失败: " + exception.getMessage());
            actionResponse.setCostPoints(0);
            actionResponse.setThrowable(exception);
            return actionResponse;
        }
        return actionResponse;
    }

    /**
     * 解析输出内容
     *
     * @param context         上下文
     * @param handlerResponse 处理结果
     * @return 解析后的结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private JsonData parseOutput(AppContext context, HandlerResponse handlerResponse) {
        // 如果配置了 JsonSchema
        if (this.hasResponseJsonSchema(context)) {
            //获取当前定义的返回结构
            JsonSchema jsonSchema = this.getOutVariableJsonSchema(context);
            JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
            JSON json = jsonSchemaParser.parse(handlerResponse.getAnswer());
            return JsonData.of(json, jsonSchema);
        } else {
            //如果还是字符串结构，就自动包一层 data 结构 @todo 需要保证prompt不要格式化结果
            return JsonData.of(handlerResponse.getAnswer());
        }
    }

    /**
     * 获取 prompt
     *
     * @param params   参数
     * @param isCustom 是否是自定义
     * @return prompt
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String getPrompt(AppContext context, Map<String, Object> params, boolean isCustom) {
        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault("PROMPT", StrUtil.EMPTY));
        List<String> promptList = StrUtil.split(prompt, "----------");
        try {
            if (!isCustom) {
                prompt = promptList.get(0);
            } else {
                prompt = promptList.get(1);
            }
            // 判断 prompt 是否为空，如果为空，抛出异常，走catch逻辑获取默认配置
            if (StrUtil.isBlank(prompt)) {
                throw new RuntimeException("用户 prompt 为空！");
            }
        } catch (Exception e) {
            try {
                log.error("用户prompt配置异常！从字典中获取默认配置！");
                List<String> defaultPromptList = getDefaultPromptList();
                if (!isCustom) {
                    prompt = defaultPromptList.get(0);
                } else {
                    prompt = defaultPromptList.get(1);
                }
                if (StrUtil.isBlank(prompt)) {
                    throw new RuntimeException("系统默认promp为空！");
                }
                // 放入到上下文中
                context.putModelVariable("PROMPT", prompt);
                // 重新获取替换后的 prompt
                prompt = String.valueOf(context.getContextVariablesValues().getOrDefault("PROMPT", StrUtil.EMPTY));
                // 如果还是为空，抛出异常
                if (StrUtil.isBlank(prompt)) {
                    throw new RuntimeException("系统默认promp为空！");
                }
            } catch (Exception exception) {
                log.error("【{}】步骤执行失败: prompt 配置异常，{}", context.getStepId(), exception.getMessage());
                throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: prompt 配置异常，请联系管理员或稍后重试！", context.getStepId());
            }
        }

        return prompt;
    }

    /**
     * 系统默认配置prompt
     *
     * @return prompt
     */
    private String sysPrompt() {
        String prompt = MapUtil.emptyIfNull(appDictionaryService.actionDefaultConfig()).getOrDefault(CreativeConstants.SYS_PROMPT, StrUtil.EMPTY);
        return prompt;
    }

    /**
     * 从字典中获取默认值
     *
     * @param key key
     * @return 默认值
     */
    private List<String> getDefaultPromptList() {
        String prompt = MapUtil.emptyIfNull(appDictionaryService.actionDefaultConfig()).getOrDefault("小红书生成", StrUtil.EMPTY);
        List<String> promptList = StrUtil.split(prompt, "----------");
        if (promptList.size() < 2) {
            throw new RuntimeException("系统默认promp配置异常！请检查字典配置：小红书生成");
        }
        return promptList;
    }

    /**
     * 处理参考内容
     *
     * @param referList 参考内容
     * @return 处理后的参考内容
     */
    private String generateRefers(List<AbstractCreativeMaterialDTO> referList) {
        try {
            StringJoiner sj = new StringJoiner("\n");
            for (AbstractCreativeMaterialDTO materialDTO : referList) {
                sj.add(JsonUtils.toJsonString(materialDTO));
                JSONObject entries = JSONUtil.parseObj(materialDTO);
                JSONArray imitateTypeJSON = entries.getJSONArray("imitateType");
                if (Objects.isNull(imitateTypeJSON)) {
                    continue;
                }
                List<String> imitateType = imitateTypeJSON.toList(String.class);
                sj.add("模仿要求：模仿这条笔记的" + imitateType.stream().collect(Collectors.joining(",")));
            }
            return sj.toString();
        } catch (Exception e) {
            log.warn("generate Refers error", e);
            return JsonUtils.toJsonString(referList);
        }
    }

    /**
     * 处理参考内容
     *
     * @param referList   参考内容
     * @param refersCount 参考内容数量
     * @return 处理后的参考内容
     */
    private List<AbstractCreativeMaterialDTO> handlerReferList(List<AbstractCreativeMaterialDTO> refersList, Integer refersCount) {
        // 随机选取
        Collections.shuffle(refersList);
        List<AbstractCreativeMaterialDTO> result = refersList.stream()
                .peek(AbstractCreativeMaterialDTO::clean)
                .limit(refersCount).collect(Collectors.toList());
        int i = 0;
        if ((i = refersCount - result.size()) == 0) {
            return result;
        }
        //  补齐元素
        for (int j = 0; j < i; j++) {
            result.add(refersList.get(RandomUtil.randomInt(refersList.size())));
        }
        return result;
    }

}
