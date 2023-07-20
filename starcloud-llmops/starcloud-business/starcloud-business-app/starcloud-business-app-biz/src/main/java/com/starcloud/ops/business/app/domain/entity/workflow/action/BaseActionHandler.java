package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.bean.BeanUtil;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public abstract class BaseActionHandler<Q, R> {

    private String name;

    private String description;

    private AppContext appContext;


    /**
     * 获取入参定义
     *
     * @return
     */
    public abstract Class<Q> getInputCls();

    /**
     * 获取出参定义
     *
     * @return
     */
    public abstract Class<R> getOutputCls();

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

        //拿到之前的结构，全部的数据
        Map<String, Object> stepParams = this.appContext.getContextVariablesValues();

        //只拿新参数

        //用新参数 覆盖老结构的参数
        context.getJsonData();

        //优化方案：老结构数据全部废除，实体初始化好配置的参数，前端只传必要的参数
        //还是 传入的参数 覆盖 保存的参数

        Q request = BeanUtil.toBean(new HashMap<String, Object>(){{
            put("stepParams", stepParams);
        }}, this.getInputCls());

        return this._execute(request);
    }
}
