package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 拼接文本 action
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent
public class AssembleActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "AssembleActionHandler", invoke = @Invoke(timeout = 180000))
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
        return 0;
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute() {

        //获取所有上游信息
        final Map<String, Object> objectMap = this.getAppContext().getContextVariablesValues();


        Map<String, Object> params = this.getAppContext().getContextVariablesValues();

        //获取到 参考文案
        String json = (String) params.get("格式要求");


        log.info("执行开始: 请求参数：\n{}");

        ActionResponse response = null;
        log.info("OpenAI ChatGPT Action 执行结束: 响应结果：\n {}", JSONUtil.parse(response).toStringPretty());
        return response;
    }


}