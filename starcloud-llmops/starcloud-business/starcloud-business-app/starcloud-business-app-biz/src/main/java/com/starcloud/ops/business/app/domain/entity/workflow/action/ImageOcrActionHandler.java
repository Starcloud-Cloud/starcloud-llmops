package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.ocr.OcrResult;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.ocr.AliyunOcrManager;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.IMAGE_OCR_ERROR;

@Slf4j
@TaskComponent
public class ImageOcrActionHandler extends BaseActionHandler {

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
        String imageUrlStr = params.get(CreativeConstants.IMAGE_OCR_URL).toString();
        long start = System.currentTimeMillis();
        OcrResult ocrResult = ALIYUN_OCR_MANAGER.recognizeGeneral(imageUrlStr);
        if (!ocrResult.isSuccess()) {
            throw exception(IMAGE_OCR_ERROR, ocrResult.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("image ocr {} ms", end - start);
        SseEmitter sseEmitter = context.getSseEmitter();
        if (Objects.nonNull(sseEmitter)) {
            StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
            callBackHandler.onLLMNewToken(JSONUtil.toJsonStr(ocrResult.getOcrGeneralDTO()));
        }

        return response(ocrResult.getOcrGeneralDTO(), context, 1);
    }

    private ActionResponse response(OcrGeneralDTO ocrGeneralDTO, AppContext context, int cost) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setAnswer(JsonUtils.toJsonString(ocrGeneralDTO));
        actionResponse.setOutput(JsonData.of(ocrGeneralDTO));
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
}