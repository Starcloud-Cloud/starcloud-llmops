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
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
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
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
//@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class MaterialActionHandler extends BaseActionHandler {

    private static final ThreadLocal<JsonSchema> JSON_SCHEMA = new TransmittableThreadLocal<>();

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
     * 校验步骤
     *
     * @param wrapper      步骤包装器
     * @param validateType 校验类型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {
        if (!ValidateTypeEnum.EXECUTE.equals(validateType)) {
            return Collections.emptyList();
        }

        List<Verification> verifications = new ArrayList<>();
        // 获取到资料库类型
        String usageModel = wrapper.getVariableToString(CreativeConstants.MATERIAL_USAGE_MODEL);
        usageModel = StringUtils.defaultIfBlank(usageModel, MaterialUsageModel.FILTER_USAGE.name());
        wrapper.putVariable(CreativeConstants.MATERIAL_USAGE_MODEL, usageModel);

        if (MaterialUsageModel.SELECT.name().equals(usageModel)) {
            String query = wrapper.getVariableToString(CreativeConstants.SELECT_MATERIAL_QUERY);
            if (StringUtils.isBlank(query)) {
                VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(), "选择执行时需要选择您需要执行的素材");
                return verifications;
            }
            MaterialLibrarySliceAppReqVO materialListRequest = JsonUtils.parseObject(query, MaterialLibrarySliceAppReqVO.class);
            if (Objects.isNull(materialListRequest)) {
                VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(), "选择执行时需要选择您需要执行的素材");
                return verifications;
            }
            // 如果没有选择素材库，则抛出异常
            if (CollectionUtil.isEmpty(materialListRequest.getSliceIdList())) {
                VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(), "选择执行时需要选择您需要执行的素材");
                return verifications;
            }
            if (materialListRequest.getSliceIdList().size() > 32) {
                VerificationUtils.addVerificationStep(verifications, wrapper.getStepCode(), "选择执行时最多选择32个素材");
                return verifications;
            }

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
     * 具体步骤执行器的入参定义的{@code JsonSchema}
     *
     * @param stepWrapper 当前步骤包装器
     * @return 具体步骤执行器的入参定义的 {@code JsonSchema}
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        //不用返回入参
        return null;
    }

    /**
     * 具体步骤执行器的出参定义的{@code JsonSchema}
     *
     * @param stepWrapper 当前步骤包装器
     * @return 具体步骤执行器的出参定义的 {@code JsonSchema}
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        return new ObjectSchema();
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
        loggerBegin(context, "素材上传步骤");

        // 获取所有上游信息
        Map<String, Object> params = context.getContextVariablesValues();

        // 获取到资料库类型
        String businessType = (String) params.get(CreativeConstants.BUSINESS_TYPE);

        // 获取到处理好的素材库字段设置
        String materialListString = (String) params.get(CreativeConstants.MATERIAL_LIST);
        List<Map<String, Object>> materialList = MaterialDefineUtil.parseData(materialListString);

        // 构造结构
        JsonDocsDefSchema jsonDocsDefSchema = new JsonDocsDefSchema();
        jsonDocsDefSchema.setDocs(CollectionUtil.emptyIfNull(materialList));

        // 转换响应结果
        ActionResponse response = convert(params, businessType);
        response.setAnswer(JsonUtils.toJsonPrettyString(response.getAnswer()));
        response.setOutput(JsonData.of(jsonDocsDefSchema, new ObjectSchema()));

        // 结束日志打印
        loggerSuccess(context, response, "素材上传步骤");
        JSON_SCHEMA.remove();
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
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setIsShow(Boolean.FALSE);
        response.setAnswer(answer);
        response.setOutput(JsonData.of(answer));
        response.setMessage(" ");
        response.setStepConfig(params);
        response.setMessageTokens(0L);
        response.setMessageUnitPrice(BigDecimal.ZERO);
        response.setAnswerTokens(0L);
        response.setAnswerUnitPrice(BigDecimal.ZERO);
        response.setTotalTokens(0L);
        response.setTotalPrice(BigDecimal.ZERO);
        response.setCostPoints(0);
        return response;
    }
}
