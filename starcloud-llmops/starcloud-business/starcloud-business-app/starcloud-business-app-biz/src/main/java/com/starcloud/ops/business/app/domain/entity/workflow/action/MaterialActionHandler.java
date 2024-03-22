package com.starcloud.ops.business.app.domain.entity.workflow.action;

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
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
        // 获取到资料库类型
        String materialType = (String) params.get(CreativeConstants.MATERIAL_TYPE);
        // 转换响应结果
        ActionResponse response = convert(materialType);

        //把图片生成节点的素材信息 复制到这里，符合变量的替换逻辑
        final Map<String, Object> posterParams = this.getAppContext().getContextVariablesValues(PosterActionHandler.class);

        // 获取到处理好的上传素材
        String posterStyle = (String) posterParams.get(CreativeConstants.POSTER_STYLE);
        PosterStyleDTO posterStyleDTO = JsonUtils.parseObject(posterStyle, PosterStyleDTO.class);

        JsonDocsDefSchema jsonDocsDefSchema = new JsonDocsDefSchema();

        if (posterStyleDTO != null) {
            jsonDocsDefSchema.setDocs(posterStyleDTO.getMaterialList());
        }
        //保持跟返回结果一样的JsonSchema
        JsonSchema outJsonSchema = this.getOutVariableJsonSchema(this.getAppContext().getStepWrapper());
        response.setOutput(JsonData.of(jsonDocsDefSchema, outJsonSchema));

        log.info("MaterialActionHandler 执行结束: 响应结果：\n {}", JsonUtils.toJsonPrettyString(response));
        return response;
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

        // 获取到资料库类型
        String materialType = workflowStepWrapper.getVariablesValue(CreativeConstants.MATERIAL_TYPE);

        //构造一层 array schema
        ObjectSchema docSchema = (ObjectSchema) JsonSchemaUtils.generateJsonSchema(JsonDocsDefSchema.class);
        docSchema.setTitle(workflowStepWrapper.getStepCode());
        docSchema.setDescription(workflowStepWrapper.getDescription());

        ArraySchema arraySchema = (ArraySchema) docSchema.getProperties().get("docs");

        ObjectSchema materialSchema = (ObjectSchema) JsonSchemaUtils.generateJsonSchema(MaterialTypeEnum.of(materialType).getAClass());
        arraySchema.setItemsSchema(materialSchema);

        return docSchema;
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
    private ActionResponse convert(String answer) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setAnswer(answer);
        actionResponse.setOutput(JsonData.of(answer));
        actionResponse.setMessage(JsonUtils.toJsonString(this.getAppContext().getContextVariablesValues()));
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
