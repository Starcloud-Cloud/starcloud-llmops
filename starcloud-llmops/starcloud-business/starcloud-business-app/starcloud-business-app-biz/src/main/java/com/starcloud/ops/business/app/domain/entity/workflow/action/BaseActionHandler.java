package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public abstract class BaseActionHandler<Q, R> {

    private static UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private String name;

    private String description;

    private AppContext appContext;


    /**
     * 执行步骤
     */
    protected abstract ActionResponse _execute(Q request);


    /**
     * 流程执行器，action执行入口
     *
     * @param context
     * @param scopeDataOperator
     * @return
     */
    protected ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {

        this.appContext = context;

        Q request = this.parseInput();

        ActionResponse actionResponse = this._execute(request);

        //权益放在此处是为了准确的扣除权益 并且控制不同action不同权益的情况
        if (actionResponse.getSuccess() && this.getBenefitsType() != null && actionResponse.getTotalTokens() > 0) {
            //权益记录
            userBenefitsService.expendBenefits(this.getBenefitsType().getCode(), actionResponse.getTotalTokens(), context.getUserId(), context.getConversationId());
        }
        // 执行结果覆盖
        this.appContext.buildActionResponse(actionResponse);
        context.buildActionResponse(actionResponse);
        return actionResponse;
    }


    protected Q parseInput() {


        Map<String, Object> stepParams = this.appContext.getContextVariablesValues();

        //只拿新参数

        //用新参数 覆盖老结构的参数
        this.getAppContext().getJsonData();

        //优化方案：老结构数据全部废除，实体初始化好配置的参数，前端只传必要的参数
        //还是 传入的参数 覆盖 保存的参数

        Type query = TypeUtil.getTypeArgument(this.getClass());
        Class<Q> inputCls = (Class<Q>) query;

        Q request = BeanUtil.toBean(new HashMap<String, Object>() {{
            put("stepParams", stepParams);
        }}, inputCls);

        return request;
    }

    /**
     * 获取当前handler消耗的权益类型，如果返回自动扣除权益，返回null,则不处理权益扣除
     *
     * @return
     */
    protected BenefitsTypeEnums getBenefitsType() {
        return BenefitsTypeEnums.TOKEN;
    }

    protected String getAppUid() {
        return this.getAppContext().getApp().getUid();
    }
}
