package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.model.content.CopyWritingContent;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * 获取输出变量的 JSON Schema
     *
     * @param stepWrapper 步骤包装器
     * @return 输出变量的 JSON Schema
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        // 因为输出变量是固定的，先直接返回，数据库中的值，没有描述信息。
        // todo 等到此处返回结果的jsonschema 可以自定义的时候，此处需要进行重新处理。
        return JsonSchemaUtils.generateCopyWritingJsonSchema();
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
        // 如果不是执行校验类型，则直接返回
        if (!ValidateTypeEnum.EXECUTE.equals(validateType)) {
            return Collections.emptyList();
        }

        List<Verification> verifications = new ArrayList<>();
        Object titleObject = wrapper.getVariablesValue(CreativeConstants.TITLE);
        if (Objects.isNull(titleObject) || StringUtils.isBlank(String.valueOf(titleObject))) {
            VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(),
                    "【" + wrapper.getName() + "】步骤标题变量不能为空！");
        }
        Object contentObject = wrapper.getVariablesValue(CreativeConstants.CONTENT);
        if (Objects.isNull(contentObject) || StringUtils.isBlank(String.valueOf(contentObject))) {
            VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(),
                    "【" + wrapper.getName() + "】步骤内容变量不能为空！");
        }

        return verifications;
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
        loggerBegin(context, "笔记生成步骤");
        // 获取所有上游信息
        Map<String, Object> params = context.getContextVariablesValues();
        // 获取到参考文案标题
        String title = String.valueOf(params.get(CreativeConstants.TITLE));
        // 获取到参考文案内容
        String content = String.valueOf(params.get(CreativeConstants.CONTENT));
        // 获取到标签列表
        List<String> tagList = getTagList(params);

        // 组装文案内容
        CopyWritingContent copyWriting = new CopyWritingContent();
        copyWriting.setTitle(title);
        copyWriting.setContent(content);
        copyWriting.setTagList(tagList);

        // 转换响应结果
        ActionResponse response = convert(params, copyWriting);

        // 结束日志打印
        loggerSuccess(context, response, "笔记生成步骤");
        return response;
    }

    /**
     * 获取标签列表
     *
     * @param params 参数
     * @return 标签列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<String> getTagList(Map<String, Object> params) {
        // 获取到标签
        String tag = String.valueOf(params.get(CreativeConstants.TAG_LIST));
        if (StringUtils.isBlank(tag) || "null".equalsIgnoreCase(tag)) {
            tag = "[]";
        }
        try {
            return JSONUtil.toList(tag, String.class);
        } catch (Exception e) {
            // 返回空数组
            return Collections.emptyList();
        }
    }

    /**
     * 转换响应结果
     *
     * @param params      参数
     * @param copyWriting 文案内容
     * @return 转换后的响应结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(Map<String, Object> params, CopyWritingContent copyWriting) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(true);
        actionResponse.setAnswer(JsonUtils.toJsonPrettyString(copyWriting));
        actionResponse.setOutput(JsonData.of(copyWriting, CopyWritingContent.class));
        actionResponse.setMessage(" ");
        actionResponse.setStepConfig(params);
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(BigDecimal.ZERO);
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(BigDecimal.ZERO);
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(BigDecimal.ZERO);
        actionResponse.setCostPoints(0);
        return actionResponse;
    }

}
