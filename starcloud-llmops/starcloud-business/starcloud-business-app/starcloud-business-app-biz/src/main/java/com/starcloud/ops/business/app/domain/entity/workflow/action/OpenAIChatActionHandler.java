package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent
public class OpenAIChatActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "OpenAIChatActionHandler", invoke = @Invoke(timeout = 180000))
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
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }

    /**
     * 获取应用执行模型
     *
     * @param context 上下文
     * @return 应用执行模型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getLlmModelType(AppContext context) {
        String llmModelType = super.getLlmModelType(context);
        return TokenCalculator.fromName(llmModelType).getName();
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param request 请求参数
     * @param context
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute(AppContext context) {
        // 开始日志打印
        loggerBegin(context, "AI生成步骤");

        // 获取执行参数
        Map<String, Object> params = context.getContextVariablesValues();
        String model = this.getLlmModelType(context);
        Integer n = Optional.ofNullable(context.getN()).orElse(1);
        String prompt = String.valueOf(params.getOrDefault(AppConstants.PROMPT, "hi, what you name?"));
        Integer maxTokens = Integer.valueOf((String) params.getOrDefault(AppConstants.MAX_TOKENS, "1000"));
        Double temperature = Double.valueOf((String) params.getOrDefault(AppConstants.TEMPERATURE, "0.7d"));

        // 参数日志打印
        loggerParamter(context, params, "AI生成步骤");

        // 构建AI生成请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(context.getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);
        handlerRequest.setN(n);

        // 构建AI生成请求上下文
        HandlerContext handlerContext = HandlerContext.createContext(
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                context.getEndUserId(),
                context.getScene(),
                handlerRequest
        );

        // 构建AI生成处理器
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);

        // 执行AI生成处理器
        HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
        ActionResponse response = convert(context, handlerResponse);

        // 结束日志打印
        loggerSuccess(context, response, "AI生成步骤");

        return response;
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(AppContext context, HandlerResponse handlerResponse) {
        // 计算权益点数
        Long tokens = handlerResponse.getMessageTokens() + handlerResponse.getAnswerTokens();
        String llmModelType = this.getLlmModelType(context);
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(llmModelType, tokens);

        ActionResponse response = new ActionResponse();
        response.setSuccess(handlerResponse.getSuccess());
        response.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        response.setErrorMsg(handlerResponse.getErrorMsg());
        response.setType(handlerResponse.getType());
        response.setIsShow(true);
        response.setMessage(handlerResponse.getMessage());
        response.setAnswer(handlerResponse.getAnswer());
        response.setOutput(JsonData.of(handlerResponse.getOutput()));
        response.setMessageTokens(handlerResponse.getMessageTokens());
        response.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        response.setAnswerTokens(handlerResponse.getAnswerTokens());
        response.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        response.setTotalTokens(handlerResponse.getTotalTokens());
        response.setTotalPrice(handlerResponse.getTotalPrice());
        response.setAiModel(llmModelType);
        response.setStepConfig(handlerResponse.getStepConfig());
        response.setCostPoints(handlerResponse.getSuccess() ? costPoints : 0);

        return response;
    }


}
