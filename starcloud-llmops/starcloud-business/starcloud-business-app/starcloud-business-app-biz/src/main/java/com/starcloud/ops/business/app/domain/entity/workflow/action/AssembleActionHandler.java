package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 拼接文本 action
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "ActionResponseHandler")
public class AssembleActionHandler extends BaseActionHandler<Void, Void> {


    @NoticeVar
    @TaskService(name = "ActionResponseHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 获取当前handler消耗的权益点数
     *
     * @param request 请求参数
     * @return 权益点数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Integer getCostPoints(Void request) {

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
    protected ActionResponse doExecute(Void request) {

        //获取所有上游信息
        this.getAppContext();

        log.info("执行开始: 请求参数：\n{}", JSONUtil.parse(request).toStringPretty());

        ActionResponse response = null;
        log.info("OpenAI ChatGPT Action 执行结束: 响应结果：\n {}", JSONUtil.parse(response).toStringPretty());
        return response;
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @param request         请求参数
     * @return 转换后的响应结果
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(HandlerResponse handlerResponse, Request request) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());
        actionResponse.setAnswer(handlerResponse.getAnswer());
        actionResponse.setOutput(JsonData.of(handlerResponse.getOutput()));
        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        // 权益点数, 成功正常扣除, 失败不扣除
        actionResponse.setCostPoints(handlerResponse.getSuccess() ? this.getCostPoints(null) : 0);
        return actionResponse;
    }

}
