package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerReq;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.ocr.OcrResult;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.VariableDefInterface;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.recommend.RecommendResponseFactory;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.ocr.AliyunOcrManager;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Slf4j
@TaskComponent
public class ImageOcrActionHandler extends BaseActionHandler implements VariableDefInterface {


    private static final AliyunOcrManager ALIYUN_OCR_MANAGER = SpringUtil.getBean(AliyunOcrManager.class);

    @NoticeVar
    @TaskService(name = "ImageOcrActionHandler", invoke = @Invoke(timeout = 180000))
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
    public void validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {

    }

    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }

    @Override
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        HandlerReq handlerReq = JSONUtil.toBean((String) params.get(CreativeConstants.IMAGE_OCR_URL), HandlerReq.class);

        List<String> urls = handlerReq.getImageUrls();

        List<OcrResult> result = Optional.ofNullable(urls).orElse(new ArrayList<>()).stream().map((url) -> {

            long start = System.currentTimeMillis();

            OcrResult ocrResult = ALIYUN_OCR_MANAGER.recognizeGeneral(url);

            if (Objects.nonNull(ocrResult) && Objects.nonNull(ocrResult.getOcrGeneralDTO())) {
                ocrResult.getOcrGeneralDTO().setCleansingContent("");
            }

            long end = System.currentTimeMillis();

            log.info("image ocr [{}], {} ms", url, end - start);

            return ocrResult;

        }).collect(Collectors.toList());

        HandlerResponse response = new HandlerResponse();
        response.setList(result);

        SseEmitter sseEmitter = context.getSseEmitter();
        if (Objects.nonNull(sseEmitter)) {
            StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
            callBackHandler.onLLMNewToken(JSONUtil.toJsonStr(response));
        }

        return response(response, context, CollectionUtil.size(result));
    }

    private ActionResponse response(HandlerResponse response, AppContext context, int cost) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));

        actionResponse.setAnswer(JsonUtils.toJsonString(response));
        actionResponse.setOutput(JsonData.of(response, HandlerResponse.class));

        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setStepConfig(context.getContextVariablesValues());
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(new BigDecimal("0"));
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(new BigDecimal("0"));
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(new BigDecimal("0"));
        actionResponse.setAiModel(null);
        // 一个图片OCR一次 一个点
        actionResponse.setCostPoints(cost);
        return actionResponse;
    }


    /**
     * 节点参数都是固定的
     *
     * @param stepWrapper 当前步骤包装器
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @Override
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper stepWrapper) {

        return null;
    }


    /**
     * 节点参数都是固定的
     *
     * @param stepWrapper 当前步骤包装器
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @Override
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        return null;
    }


    /**
     * 节点参数都是固定的
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @Override
    public JsonSchema inVariableJsonSchema() {

        return JsonSchemaUtils.generateJsonSchema(HandlerReq.class);
    }


    /**
     * 节点参数都是固定的
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @Override
    public JsonSchema outVariableJsonSchema() {
        return JsonSchemaUtils.generateJsonSchema(HandlerResponse.class);
    }

    /**
     * 返回 节点详细定义
     *
     * @return
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepRespVO defWorkflowStepResp() {

        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName("图片ocr");
        step.setDescription("图片ocr");
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(ImageOcrActionHandler.class.getSimpleName());

        JsonDataVO jsonData = JsonDataVO.of(JsonSchemaUtils.generateJsonSchemaStr(this.outVariableJsonSchema()));

        step.setResponse(RecommendResponseFactory.defJsonResponse(Boolean.TRUE, Boolean.TRUE, jsonData));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.STEP_VERSION_V2);
        step.setIcon("image-ocr");
        step.setTags(Arrays.asList("image", "ocr"));
        step.setScenes(AppUtils.DEFAULT_SCENES);

        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        variable.setJsonSchema(JsonSchemaUtils.generateJsonSchemaStr(this.inVariableJsonSchema()));

        step.setVariable(variable);
        return step;
    }
}
