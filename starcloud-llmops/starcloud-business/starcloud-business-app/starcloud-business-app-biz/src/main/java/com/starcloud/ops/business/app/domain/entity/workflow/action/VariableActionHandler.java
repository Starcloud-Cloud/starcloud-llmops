package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

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
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @Override
    protected Integer getCostPoints() {
        return 0;
    }

    /**
     * 执行具体的步骤
     *
     * @return 执行结果
     */
    @Override
    protected ActionResponse doExecute() {
        log.info("VariableActionHandler doExecute");

        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(true);
        actionResponse.setType(AppStepResponseTypeEnum.TEXT.name());
        actionResponse.setIsShow(true);
        actionResponse.setMessage("variable");
        actionResponse.setAnswer("variable");
        actionResponse.setOutput(JsonData.of("variable"));
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(new BigDecimal("0"));
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(new BigDecimal("0"));
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(new BigDecimal("0"));
        actionResponse.setStepConfig("{}");
        actionResponse.setCostPoints(this.getCostPoints());

        log.info("VariableActionHandler doExecute end");
        return actionResponse;
    }
}