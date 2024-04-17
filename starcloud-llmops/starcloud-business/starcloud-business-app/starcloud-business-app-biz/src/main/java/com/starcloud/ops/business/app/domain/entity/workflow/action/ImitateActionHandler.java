package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@TaskComponent
public class ImitateActionHandler extends BaseActionHandler {

    @NoticeVar
    @TaskService(name = "ImitateActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }


    @Override
    protected ActionResponse doExecute() {
        Map<String, Object> params = this.getAppContext().getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：请求参数：\n{}", this.getClass().getSimpleName(), this.getAppContext().getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeSchemeGenerateModeEnum.AI_PARODY.name()));


        // AI仿写模式
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(generateMode)) {
            return this.doAiParodyExecute(params);
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
    private ActionResponse doAiParodyExecute(Map<String, Object> params) {
        String generateMode = CreativeSchemeGenerateModeEnum.AI_PARODY.name();
        log.info("自定义内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // 获取到参考内容
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS_IMITATE, "[]"));
        List<AbstractCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractCreativeMaterialDTO.class);

        // 需要交给 ChatGPT 的参考内容数量
        Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.REFERS_COUNT, "3")));

        // 处理参考内容
        List<AbstractCreativeMaterialDTO> handlerReferList = handlerReferList(referList, refersCount);
        this.getAppContext().putVariable(CreativeConstants.REFERS, JsonUtils.toJsonString(handlerReferList));
        this.getAppContext().putVariable(CreativeConstants.REFERS_IMITATE, generateRefers(handlerReferList));

        // 重新获取上下文处理参数，因为参考内容已经被处理了，需要重新获取
        params = this.getAppContext().getContextVariablesValues();
        log.info("自定义内容生成[{}][{}]：正在执行：处理之后请求参数：\n{}", this.getClass().getSimpleName(), this.getAppContext().getStepId(), JsonUtils.toJsonPrettyString(params));

        // 获取到大模型 model
        String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);

        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));

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

            JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
            JSONObject jsonObject = jsonSchemaParser.parse(actionResponse.getAnswer());

            actionResponse.setOutput(JsonData.of(jsonObject, jsonSchema));
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
