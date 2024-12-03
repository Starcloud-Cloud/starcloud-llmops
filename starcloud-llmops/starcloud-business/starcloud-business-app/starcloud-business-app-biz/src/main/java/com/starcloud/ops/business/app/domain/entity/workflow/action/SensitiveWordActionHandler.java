package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.green20220302.models.TextModerationRequest;
import com.aliyun.green20220302.models.TextModerationResponse;
import com.aliyun.green20220302.models.TextModerationResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.starcloud.ops.business.app.api.plugin.AliRiskReason;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.RiskWordReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.RiskWordRespVO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.util.SensitiveWordUtil;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.RISK_WORD_ERROR;

@Slf4j
@TaskComponent
public class SensitiveWordActionHandler extends BaseActionHandler {

    private static final com.aliyun.green20220302.Client client = SpringUtil.getBean(com.aliyun.green20220302.Client.class);

    @NoticeVar
    @TaskService(name = "SensitiveWordActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    @Override
    public List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return null;
    }

    @Override
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        String request = String.valueOf(params.get(CreativeConstants.SENSITIVE_WORD));
        RiskWordReqVO reqVO = JSONUtil.toBean(request, RiskWordReqVO.class);

        List<String> checkedFieldList = reqVO.getCheckedFieldList();
        List<Map<String, Object>> materialList = reqVO.getMaterialList();

        List<Map<String, Object>> pendingMaterialList = new ArrayList<>(materialList.size());

        for (Map<String, Object> material : materialList) {
            HashMap<String, Object> pendingMaterial = new HashMap<>();
            for (String field : checkedFieldList) {
                pendingMaterial.put(field, material.get(field));
            }
            pendingMaterial.put("id", material.get("id"));
            pendingMaterialList.add(pendingMaterial);
        }

        String sourceContent = JSONUtil.toJsonStr(pendingMaterialList);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("content", sourceContent);
        TextModerationRequest textModerationRequest = new TextModerationRequest()
                .setService("comment_detection")
                .setServiceParameters(JSONUtil.toJsonStr(parameters));
        TextModerationResponse textModerationResponse = null;
        try {
            textModerationResponse = client.textModerationWithOptions(textModerationRequest, new RuntimeOptions());
        } catch (Exception e) {
            log.error("请求阿里云失败 {}", e.getMessage());
            throw exception(RISK_WORD_ERROR, e.getMessage());
        }
        if (!Objects.equals(textModerationResponse.getStatusCode(), 200)) {
            log.error("请求阿里云失败 {}", JSONUtil.toJsonPrettyStr(textModerationResponse));
            throw exception(RISK_WORD_ERROR, JSONUtil.toJsonStr(textModerationResponse.getBody()));
        }

        String reason = Optional.ofNullable(textModerationResponse.getBody())
                .map(TextModerationResponseBody::getData)
                .map(TextModerationResponseBody.TextModerationResponseBodyData::getReason).orElse(StringUtils.EMPTY);
        if (StringUtil.isBlank(reason)) {
            // 没有违禁词
            RiskWordRespVO riskWordRespVO = new RiskWordRespVO(pendingMaterialList, null);
            return response(context, riskWordRespVO);
        }

        AliRiskReason riskReason = JSONUtil.toBean(reason, AliRiskReason.class);

        StringJoiner stringJoiner = new StringJoiner(",");
        if (StringUtil.isNotBlank(riskReason.getRiskWords())) {
            stringJoiner.add(riskReason.getRiskWords());
        }
        if (StringUtil.isNotBlank(riskReason.getCustomizedWords())) {
            stringJoiner.add(riskReason.getCustomizedWords());
        }
        if (Objects.equals("low", riskReason.getRiskLevel())
                || stringJoiner.length() == 0) {
            log.warn("低风险，不替换 {}", reason);
            RiskWordRespVO riskWordRespVO = new RiskWordRespVO(pendingMaterialList, riskReason);
            return response(context, riskWordRespVO);
        }

        String[] riskWords = stringJoiner.toString().split(",");
        for (String riskWord : riskWords) {
            sourceContent = SensitiveWordUtil.replace(sourceContent, riskWord, reqVO.getProcessManner());
        }
        List<Map<String, Object>> cleanMaterial = JSONObject.parseObject(sourceContent, new TypeReference<List<Map<String, Object>>>() {
        });
        RiskWordRespVO riskWordRespVO = new RiskWordRespVO(cleanMaterial, riskReason);
        return response(context, riskWordRespVO);
    }

    private ActionResponse response(AppContext context, RiskWordRespVO result) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setAnswer(JsonUtils.toJsonString(result));
        actionResponse.setOutput(JsonData.of(result));
        actionResponse.setStepConfig(context.getContextVariablesValues());
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(new BigDecimal("0"));
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(new BigDecimal("0"));
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(new BigDecimal("0"));
        actionResponse.setAiModel(null);
//        actionResponse.setCostPoints(cost);
        return actionResponse;
    }
}
