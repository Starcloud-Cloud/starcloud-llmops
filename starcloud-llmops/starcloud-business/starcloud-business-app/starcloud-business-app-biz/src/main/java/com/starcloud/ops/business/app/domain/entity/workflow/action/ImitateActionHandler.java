package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
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
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@TaskComponent
@Deprecated
public class ImitateActionHandler extends BaseActionHandler {

    @NoticeVar
    @TaskService(name = "ImitateActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 校验步骤
     *
     * @param wrapper      步骤包装器
     * @param validateType 校验类型
     */
    @Override
    public List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }

    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }


    @Override
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：请求参数：\n{}", this.getClass().getSimpleName(), context.getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeContentGenerateModelEnum.AI_PARODY.name()));


        // AI仿写模式
        if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generateMode)) {
            return this.doAiParodyExecute(context, params);
        }

        // 不支持的生成模式
        return ActionResponse.failure("310100020", "自定义内容生成不支持的生成模式: " + generateMode, params);

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
        String generateMode = CreativeContentGenerateModelEnum.AI_PARODY.name();
        log.info("自定义内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // 获取到参考内容
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS_IMITATE, "[]"));
        List<AbstractCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractCreativeMaterialDTO.class);

        // 需要交给 ChatGPT 的参考内容数量
        Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.REFERS_COUNT, "3")));

        // 处理参考内容
        List<AbstractCreativeMaterialDTO> handlerReferList = handlerReferList(referList, refersCount);
        context.putVariable(CreativeConstants.REFERS, JsonUtils.toJsonString(handlerReferList));
        context.putVariable(CreativeConstants.REFERS_IMITATE, generateRefers(handlerReferList));

        // 重新获取上下文处理参数，因为参考内容已经被处理了，需要重新获取
        params = context.getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：处理之后请求参数：\n{}", this.getClass().getSimpleName(), context.getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getLlmModelType(context)).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(context.getN()).orElse(1);

        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));

        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault("MAX_TOKENS", "1000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault("TEMPERATURE", "0.7")));

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
    private ActionResponse doGenerateExecute(AppContext context, OpenAIChatHandler.Request handlerRequest) {
        // 构建请求上下文
        HandlerContext<OpenAIChatHandler.Request> handlerContext = HandlerContext.createContext(
                context.getUid(),
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
        actionResponse.setAiModel(Optional.ofNullable(this.getLlmModelType(context)).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName()));

        // 计算权益点数
        Long tokens = actionResponse.getMessageTokens() + actionResponse.getAnswerTokens();
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(this.getLlmModelType(context), tokens);
        actionResponse.setCostPoints(handlerResponse.getSuccess() ? costPoints : 0);

        //如果配置了 JsonSchema
        if (this.hasResponseJsonSchema(context)) {
            //获取当前定义的返回结构
            JsonSchema jsonSchema = this.getOutVariableJsonSchema(context);

            JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
            JSON json = jsonSchemaParser.parse(actionResponse.getAnswer());

            actionResponse.setOutput(JsonData.of(json, jsonSchema));
        } else {
            actionResponse.setOutput(JsonData.of(actionResponse.getAnswer()));
        }

        return actionResponse;
    }

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
     */
    private List<AbstractCreativeMaterialDTO> handlerReferList(List<AbstractCreativeMaterialDTO> refersList, Integer refersCount) {
        // 随机选取
        if (CollectionUtils.isEmpty(refersList)) {
            return Collections.emptyList();
        }
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
