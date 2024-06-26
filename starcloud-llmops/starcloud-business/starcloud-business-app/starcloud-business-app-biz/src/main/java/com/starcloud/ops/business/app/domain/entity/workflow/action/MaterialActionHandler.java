package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
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
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
//@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class MaterialActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "MaterialActionHandler", invoke = @Invoke(timeout = 180000))
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

    @Override
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        //不用返回入参
        return null;
    }

    /**
     * 只根据配置的 素材类型，直接获取对应的 素材类型 对象的结构
     *
     * @return 输出变量的jsonSchema
     */
    @Override
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        //构造一层 array schema
        ObjectSchema docSchema = (ObjectSchema) JsonSchemaUtils.generateJsonSchema(JsonDocsDefSchema.class);
        docSchema.setTitle(workflowStepWrapper.getStepCode());
        docSchema.setDescription(workflowStepWrapper.getDescription());

        ArraySchema arraySchema = (ArraySchema) docSchema.getProperties().get("docs");

        // 素材自定义配置
        String materialDefine = workflowStepWrapper.getVariablesValue(CreativeConstants.MATERIAL_DEFINE);
        ObjectSchema materialSchema = (ObjectSchema) JsonSchemaUtils.expendGenerateJsonSchema(materialDefine);
        arraySchema.setItemsSchema(materialSchema);

        return docSchema;
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
        log.info("素材上传步骤【开始执行】: 执行步骤: {}, 应用UID: {}",
                context.getStepId(), context.getUid());

        // 获取所有上游信息
        Map<String, Object> params = context.getContextVariablesValues();
        // 获取到资料库类型
        String businessType = (String) params.get(CreativeConstants.BUSINESS_TYPE);
        // 转换响应结果
        ActionResponse response = convert(params, businessType);

        // 获取到处理好的上传素材
        String materialListString = (String) params.get(CreativeConstants.MATERIAL_LIST);
        List<Map<String, Object>> materialList = MaterialDefineUtil.parseData(materialListString);

        JsonDocsDefSchema jsonDocsDefSchema = new JsonDocsDefSchema();
        jsonDocsDefSchema.setDocs(CollectionUtil.emptyIfNull(materialList));
        //保持跟返回结果一样的JsonSchema
        JsonSchema outJsonSchema = this.getOutVariableJsonSchema(context.getStepWrapper());
        response.setAnswer(JsonUtils.toJsonPrettyString(response.getAnswer()));
        response.setOutput(JsonData.of(jsonDocsDefSchema, outJsonSchema));

        log.info("素材上传步骤【执行结束】: 执行步骤: {}, 应用UID: {}, 响应结果: \n{}",
                context.getStepId(), context.getUid(), JsonUtils.toJsonPrettyString(response));
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
    private ActionResponse convert(Map<String, Object> params, String answer) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setAnswer(answer);
        actionResponse.setOutput(JsonData.of(answer));
        actionResponse.setMessage(JsonUtils.toJsonString(params));
        actionResponse.setStepConfig(params);
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(BigDecimal.ZERO);
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(BigDecimal.ZERO);
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(BigDecimal.ZERO);
        actionResponse.setAiModel("material");
        actionResponse.setCostPoints(0);
        return actionResponse;
    }
}
