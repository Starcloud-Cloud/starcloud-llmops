package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.reference.ReferenceSchemeDTO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.ActionUtils;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
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
public class ParagraphActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "ParagraphActionHandler", invoke = @Invoke(timeout = 180000))
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
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Integer getCostPoints() {
        String aiModel = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        return CostPointUtils.obtainMagicBeanCostPoint(aiModel);
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
        log.info("段落内容生成[{}]：执行开始......", this.getClass().getSimpleName());
        Map<String, Object> params = this.getAppContext().getContextVariablesValues();
        log.info("段落内容生成[{}]：正在执行：请求参数：\n{}", this.getClass().getSimpleName(), JSONUtil.parse(params).toStringPretty());
        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeSchemeGenerateModeEnum.AI_PARODY.name()));

        // AI仿写模式
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(generateMode)) {
            return doAiParodyExecute(params);
        }

        // AI自定义模式
        if (CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(generateMode)) {
            return doAiCustomExecute(params);
        }

        // 不支持的生成模式
        return ActionResponse.failure("310100020", "段落内容生成：不支持的生成模式: " + generateMode, params);
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
        log.info("段落内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // 获取到参考内容
        String refersKey = ActionUtils.getGenerateModeParamKey(generateMode, CreativeConstants.REFERS);
        String refers = String.valueOf(params.getOrDefault(refersKey, "[]"));
        List<ReferenceSchemeDTO> referList = JSONUtil.toList(refers, ReferenceSchemeDTO.class);

        // 需要交给 ChatGPT 的参考内容数量
        String refersCountKey = ActionUtils.getGenerateModeParamKey(generateMode, CreativeConstants.REFERS_COUNT);
        Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(refersCountKey, "3")));

        // 处理参考内容
        List<ReferenceSchemeDTO> handlerReferList = handlerReferList(referList, refersCount);
        this.getAppContext().putVariable(refersKey, handlerReferList);

        // 重新获取上下文处理参数，因为参考内容已经被处理了，需要重新获取
        params = this.getAppContext().getContextVariablesValues();

        log.info("段落内容生成[{}]：正在执行：处理之后请求参数：\n{}", this.getClass().getSimpleName(), JSONUtil.parse(params).toStringPretty());

        // OpenAI 模型 和 生成数量 n 都是通过上下文传入。无法区别生成模式
        String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);

        // 获取到 prompt
        String promptKey = ActionUtils.getGenerateModeParamKey(generateMode, "PROMPT");
        String prompt = String.valueOf(params.getOrDefault(promptKey, "hi, what you name?"));

        // 获取到 maxTokens
        String maxTokensKey = ActionUtils.getGenerateModeParamKey(generateMode, "MAX_TOKENS");
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault(maxTokensKey, "1000")));

        // 获取到 temperature
        String temperatureKey = ActionUtils.getGenerateModeParamKey(generateMode, "TEMPERATURE");
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault(temperatureKey, "0.7")));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(this.getAppContext().getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(handlerRequest, params);
        log.info("段落内容生成[{}]：执行成功。生成模式: [{}], : 结果：\n{}", this.getClass().getSimpleName(),
                generateMode,
                JSONUtil.parse(actionResponse).toStringPretty()
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
        log.info("段落内容生成[{}]：生成模式：[{}]......", this.getClass().getSimpleName(), generateMode);

        // OpenAI 模型 和 生成数量 n 都是通过上下文传入。无法区别生成模式
        String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);

        // 获取到 prompt
        String promptKey = ActionUtils.getGenerateModeParamKey(generateMode, "PROMPT");
        String prompt = String.valueOf(params.getOrDefault(promptKey, "hi, what you name?"));

        // 获取到 maxTokens
        String maxTokensKey = ActionUtils.getGenerateModeParamKey(generateMode, "MAX_TOKENS");
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault(maxTokensKey, "1000")));

        // 获取到 temperature
        String temperatureKey = ActionUtils.getGenerateModeParamKey(generateMode, "TEMPERATURE");
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault(temperatureKey, "0.7")));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(this.getAppContext().getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        // 执行步骤
        ActionResponse actionResponse = this.doGenerateExecute(handlerRequest, params);
        log.info("段落内容生成[{}]：执行成功。生成模式: [{}], : 结果：\n{}", this.getClass().getSimpleName(),
                generateMode,
                JSONUtil.parse(actionResponse).toStringPretty()
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
    private ActionResponse doGenerateExecute(OpenAIChatHandler.Request handlerRequest, Map<String, Object> params) {
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
        String answer = handlerResponse.getAnswer();

        // 校验生成结果是否为空
        if (StrUtil.isBlank(answer)) {
            log.error("生成段落：响应结果为空！");
            return ActionResponse.failure("310100019", "生成段落结果为空！", params);
        }

        if (log.isDebugEnabled()) {
            log.debug("生成段落内容：原始结果：\n{}", answer);
        }

        // 校验并且解析生成结果
        List<ParagraphDTO> paragraphs = new ArrayList<>();
        try {
            paragraphs = JSONUtil.toList(answer, ParagraphDTO.class);
        } catch (Exception exception) {
            log.error("生成段落结果解析失败!: {}", exception.getMessage(), exception);
            return ActionResponse.failure("310100019", "生成段落结果解析失败！", params);
        }

        // 段落内容是否为空
        if (CollectionUtil.isEmpty(paragraphs)) {
            log.error("生成段落：响应结果为空！结果：{}", answer);
            return ActionResponse.failure("310100019", "生成段落结果为空！", params);
        }

        // 获取到生成模式
        String generatreMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeSchemeGenerateModeEnum.AI_PARODY.name()));
        // 需要生成的段落数量
        String paragraphCountKey = ActionUtils.getGenerateModeParamKey(generatreMode, CreativeConstants.PARAGRAPH_COUNT);
        Integer paragraphCount = Integer.valueOf(String.valueOf(params.getOrDefault(paragraphCountKey, "4")));
        // 段落数量是否一致
        if (paragraphs.size() != paragraphCount) {
            log.error("生成段落：生成的段落数量与要求的段落数量不一致！生成段落数量：{}, 要求段落数量：{}", paragraphs.size(), paragraphCount);
            return ActionResponse.failure("310100019", "生成的段落数量与要求的段落数量不一致！", params);
        }

        // 转换响应结果
        ActionResponse response = convert(handlerResponse);
        return response;
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(HandlerResponse<String> handlerResponse) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());

        writeLines(handlerResponse.getAnswer(), actionResponse);

        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        // 权益点数, 成功正常扣除, 失败不扣除
        actionResponse.setCostPoints(handlerResponse.getSuccess() ? this.getCostPoints() : 0);
        return actionResponse;
    }

    /**
     * 处理参考内容
     *
     * @param referList   参考内容
     * @param refersCount 参考内容数量
     * @return 处理后的参考内容
     */
    private List<ReferenceSchemeDTO> handlerReferList(List<ReferenceSchemeDTO> refersList, Integer refersCount) {
        List<ReferenceSchemeDTO> handlerReferList = new ArrayList<>();
        // 如果参考内容数量小于需要的，直接返回数量
        if (refersList.size() <= refersCount) {
            handlerReferList = refersList.stream().map(item -> {
                ReferenceSchemeDTO reference = SerializationUtils.clone(item);
                reference.setId(null);
                reference.setSource(null);
                reference.setLink(null);
                reference.setTagList(null);
                reference.setImageList(null);
                reference.setTitle(null);
                return reference;
            }).collect(Collectors.toList());
        } else {
            for (int i = 0; i < refersCount; i++) {
                ReferenceSchemeDTO reference = SerializationUtils.clone(refersList.get(RandomUtil.randomInt(refersList.size())));
                reference.setId(null);
                reference.setSource(null);
                reference.setLink(null);
                reference.setTagList(null);
                reference.setImageList(null);
                reference.setTitle(null);
                handlerReferList.add(reference);
            }
        }
        return handlerReferList;
    }

    /**
     * 写入段落内容
     *
     * @param str            段落内容
     * @param actionResponse 结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static void writeLines(String str, ActionResponse actionResponse) {
        if (StrUtil.isNotBlank(str)) {
            List<ParagraphDTO> paragraphList = JSONUtil.toList(str, ParagraphDTO.class);
            String answer = paragraphList.stream()
                    .map(paragraph -> paragraph.getParagraphTitle() + "\r\n" + paragraph.getParagraphContent())
                    .collect(Collectors.joining("\r\n\r\n"));
            actionResponse.setAnswer(answer);
            actionResponse.setOutput(JsonData.of(paragraphList));
        }
    }

}
