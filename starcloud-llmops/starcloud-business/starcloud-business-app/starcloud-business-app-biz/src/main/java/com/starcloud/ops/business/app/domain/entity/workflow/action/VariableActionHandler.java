package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@TaskComponent
public class VariableActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "VariableActionHandler", invoke = @Invoke(timeout = 180000))
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
     * 具体handler的入参定义
     *
     * @return 步骤信息
     */
    @Override
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        ObjectSchema objectSchema = workflowStepWrapper.getVariable().getJsonSchema();
        objectSchema.setTitle(workflowStepWrapper.getStepCode());
        objectSchema.setDescription(workflowStepWrapper.getDescription());
        objectSchema.setId(workflowStepWrapper.getFlowStep().getHandler());
        return objectSchema;
    }

    /**
     * 具体handler的出参定义
     *
     * @return 步骤信息
     */
    @Override
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        return null;
    }

    /**
     * 执行具体的步骤
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    protected ActionResponse doExecute(AppContext context) {
        // 开始日志打印
        loggerBegin(context, "全局变量步骤");

        Map<String, Object> params = MapUtils.emptyIfNull(context.getContextVariablesValues());
        JsonSchema jsonSchema = this.getInVariableJsonSchema(context.getStepWrapper());

        // 返回结果
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(AppStepResponseTypeEnum.TEXT.name());
        response.setStyle(AppStepResponseStyleEnum.TEXTAREA.name());
        response.setIsShow(Boolean.FALSE);
        response.setMessage("variable");
        response.setAnswer(JsonUtils.toJsonPrettyString(params));
        response.setOutput(JsonData.of(params, jsonSchema));
        response.setMessageTokens(0L);
        response.setMessageUnitPrice(BigDecimal.ZERO);
        response.setAnswerTokens(0L);
        response.setAnswerUnitPrice(BigDecimal.ZERO);
        response.setTotalTokens(0L);
        response.setTotalPrice(BigDecimal.ZERO);
        response.setStepConfig("{}");
        response.setCostPoints(0);

        // 结束日志打印
        loggerSuccess(context, response, "全局变量步骤");
        return response;
    }

}
