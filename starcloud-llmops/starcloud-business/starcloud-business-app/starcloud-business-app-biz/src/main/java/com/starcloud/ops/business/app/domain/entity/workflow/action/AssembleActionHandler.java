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
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
        // 获取所有上游信息
        final Map<String, Object> params = this.getAppContext().getContextVariablesValues();
        // 获取到参考文案标题
        String title = (String) params.get(CreativeConstants.TITLE);
        // 获取到参考文案内容
        String content = (String) params.get(CreativeConstants.CONTENT);
        CopyWritingContentDTO copyWriting = new CopyWritingContentDTO();
        copyWriting.setTitle(title);
        copyWriting.setContent(content);
        // 转换响应结果
        ActionResponse response = convert(copyWriting);
        log.info("OpenAI ChatGPT Action 执行结束: 响应结果：\n {}", JSONUtil.parse(response).toStringPretty());
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
    private ActionResponse convert(CopyWritingContentDTO copyWriting) {
        StringBuilder assemble = new StringBuilder();
        assemble.append(copyWriting.getTitle()).append("\n").append(copyWriting.getContent());
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(true);
        actionResponse.setAnswer(assemble.toString());
        actionResponse.setOutput(JsonData.of(copyWriting));
        actionResponse.setMessage(JSONUtil.toJsonStr(this.getAppContext().getContextVariablesValues()));
        actionResponse.setStepConfig(this.getAppContext().getContextVariablesValues());
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(new BigDecimal("0"));
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(new BigDecimal("0"));
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(new BigDecimal("0"));
        // 组装消耗为 0
        actionResponse.setCostPoints(0);
        return actionResponse;
    }


}
