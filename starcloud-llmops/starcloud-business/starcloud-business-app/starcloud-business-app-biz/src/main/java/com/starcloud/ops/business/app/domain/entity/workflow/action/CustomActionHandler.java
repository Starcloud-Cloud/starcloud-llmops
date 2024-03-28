package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
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
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute() {

        Map<String, Object> params = this.getAppContext().getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：请求参数：\n{}", this.getClass().getSimpleName(), this.getAppContext().getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeSchemeGenerateModeEnum.AI_PARODY.name()));

        // 随机模式
        if (CreativeSchemeGenerateModeEnum.RANDOM.name().equals(generateMode)) {
            return this.doRandomExecute(params);
        }

        // AI仿写模式
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(generateMode)) {
            return this.doAiParodyExecute(params);
        }

        // AI自定义模式
        if (CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(generateMode)) {
            return this.doAiCustomExecute(params);
        }

        // 不支持的生成模式
        return ActionResponse.failure("310100020", "自定义内容生成不支持的生成模式: " + generateMode, params);
    }

    /**
     * 随机获取模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doRandomExecute(Map<String, Object> params) {
        log.info("自定义内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), CreativeSchemeGenerateModeEnum.RANDOM.name());
        // 获取到参考文案
        String refers = String.valueOf(params.get(CreativeConstants.REFERS));
        if (StrUtil.isBlank(refers)) {
            return ActionResponse.failure("310100019", "参考内容不能为空", params);
        }
        List<AbstractBaseCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractBaseCreativeMaterialDTO.class);
        if (CollectionUtil.isEmpty(referList)) {
            return ActionResponse.failure("310100019", "参考内容不能为空", params);
        }

        AbstractBaseCreativeMaterialDTO reference = referList.get(RandomUtil.randomInt(referList.size()));
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setType(AppStepResponseTypeEnum.TEXT.name());
        actionResponse.setIsShow(Boolean.TRUE);
        actionResponse.setMessage(" ");
        actionResponse.setAnswer(reference.generateContent());
        actionResponse.setOutput(JsonData.of(reference.generateContent(), reference.getClass()));
        actionResponse.setMessageTokens((long) actionResponse.getMessage().length());
        actionResponse.setMessageUnitPrice(TokenCalculator.getUnitPrice(ModelTypeEnum.GPT_3_5_TURBO_16K, true));
        actionResponse.setAnswerTokens((long) actionResponse.getAnswer().length());
        actionResponse.setAnswerUnitPrice(TokenCalculator.getUnitPrice(ModelTypeEnum.GPT_3_5_TURBO_16K, false));
        actionResponse.setTotalTokens(actionResponse.getMessageTokens() + actionResponse.getAnswerTokens());
        actionResponse.setAiModel(Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName()));
        BigDecimal messagePrice = new BigDecimal(String.valueOf(actionResponse.getMessageTokens())).multiply(actionResponse.getMessageUnitPrice());
        BigDecimal answerPrice = new BigDecimal(String.valueOf(actionResponse.getAnswerTokens())).multiply(actionResponse.getAnswerUnitPrice());
        actionResponse.setTotalPrice(messagePrice.add(answerPrice));
        actionResponse.setStepConfig(params);

        // 计算权益点数
        Long tokens = actionResponse.getMessageTokens() + actionResponse.getAnswerTokens();
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(this.getAiModel(), tokens);

        actionResponse.setCostPoints(costPoints);
        log.info("自定义内容生成[{}]：执行成功。生成模式: [{}], : 结果：\n{}", this.getClass().getSimpleName(),
                CreativeSchemeGenerateModeEnum.RANDOM.name(),
                JsonUtils.toJsonPrettyString(actionResponse)
        );

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
    private ActionResponse doAiParodyExecute(Map<String, Object> params) {
        String generateMode = CreativeSchemeGenerateModeEnum.AI_PARODY.name();
        log.info("自定义内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // 获取到参考内容
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS, "[]"));
        List<AbstractBaseCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractBaseCreativeMaterialDTO.class);

        // 需要交给 ChatGPT 的参考内容数量
        Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.REFERS_COUNT, "3")));

        // 处理参考内容
        List<AbstractBaseCreativeMaterialDTO> handlerReferList = handlerReferList(referList, refersCount);
        AbstractBaseCreativeMaterialDTO reference = handlerReferList.get(0);
        this.getAppContext().putVariable(CreativeConstants.REFERS, JsonUtils.toJsonString(handlerReferList));

        // 重新获取上下文处理参数，因为参考内容已经被处理了，需要重新获取
        params = this.getAppContext().getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：处理之后请求参数：\n{}", this.getClass().getSimpleName(), this.getAppContext().getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);
        /*
         * 约定：prompt 为总的 prompt，包含了 AI仿写 和 AI自定义 的 prompt. 中间用 ---------- 分割
         * AI仿写为第一个 prompt
         * AI自定义为第二个 prompt
         */
        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));
        List<String> promptList = StrUtil.split(prompt, "----------");
        prompt = promptList.get(0);

        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault("MAX_TOKENS", "1000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault("TEMPERATURE", "0.7")));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(this.getAppContext().getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(handlerRequest);

        log.info("自定义内容生成[{}]：执行成功。生成模式: [{}], : 结果：\n{}", this.getClass().getSimpleName(),
                generateMode,
                JsonUtils.toJsonPrettyString(actionResponse)
        );
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
    private ActionResponse doAiCustomExecute(Map<String, Object> params) {
        String generateMode = CreativeSchemeGenerateModeEnum.AI_CUSTOM.name();
        log.info("自定义内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);
        /*
         * 约定：prompt 为总的 prompt，包含了 AI仿写 和 AI自定义 的 prompt. 中间用 ---------- 分割
         * AI仿写为第一个 prompt
         * AI自定义为第二个 prompt
         */
        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));
        List<String> promptList = StrUtil.split(prompt, "----------");
        prompt = promptList.get(1);

        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault("MAX_TOKENS", "1000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault("TEMPERATURE", "0.7")));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(this.getAppContext().getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(handlerRequest);

        log.info("自定义内容生成[{}]：执行成功。生成模式: [{}], : 结果：\n{}", this.getClass().getSimpleName(),
                generateMode,
                JsonUtils.toJsonPrettyString(actionResponse)
        );
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
    private ActionResponse doGenerateExecute(OpenAIChatHandler.Request handlerRequest) {
        // 构建请求上下文
        HandlerContext<OpenAIChatHandler.Request> handlerContext = HandlerContext.createContext(
                this.getAppUid(),
                this.getAppContext().getConversationUid(),
                this.getAppContext().getUserId(),
                this.getAppContext().getEndUserId(),
                this.getAppContext().getScene(),
                handlerRequest
        );
        // 构建OpenAI处理器
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(this.getAppContext().getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);
        // 执行OpenAI处理器
        HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
        // 转换并且返回响应结果
        return convert(handlerResponse);
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(HandlerResponse handlerResponse) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());
        actionResponse.setAnswer(handlerResponse.getAnswer());
        //actionResponse.setOutput(JsonData.of(handlerResponse.getAnswer()));
        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        actionResponse.setAiModel(Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName()));

        // 计算权益点数
        Long tokens = actionResponse.getMessageTokens() + actionResponse.getAnswerTokens();
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(this.getAiModel(), tokens);

        actionResponse.setCostPoints(handlerResponse.getSuccess() ? costPoints : 0);


        //如果配置了 JsonSchema
        if (this.hasResponseJsonSchema()) {
            //获取当前定义的返回结构
            JsonSchema jsonSchema = this.getOutVariableJsonSchema();
            log.info("自定义内容JSON生成结果:\n{}", actionResponse.getAnswer());

            JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
            JSONObject jsonObject = jsonSchemaParser.parse(actionResponse.getAnswer());

            actionResponse.setOutput(JsonData.of(jsonObject, jsonSchema));
        } else {
            //如果还是字符串结构，就自动包一层 data 结构 @todo 需要保证prompt不要格式化结果
            actionResponse.setOutput(JsonData.of(actionResponse.getAnswer()));
        }

        return actionResponse;
    }

    /**
     * 处理参考内容
     *
     * @param referList   参考内容
     * @param refersCount 参考内容数量
     * @return 处理后的参考内容
     */
    private List<AbstractBaseCreativeMaterialDTO> handlerReferList(List<AbstractBaseCreativeMaterialDTO> refersList, Integer refersCount) {
        // 随机选取
        Collections.shuffle(refersList);
        List<AbstractBaseCreativeMaterialDTO> result = refersList.stream()
                .peek(AbstractBaseCreativeMaterialDTO::clean)
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
