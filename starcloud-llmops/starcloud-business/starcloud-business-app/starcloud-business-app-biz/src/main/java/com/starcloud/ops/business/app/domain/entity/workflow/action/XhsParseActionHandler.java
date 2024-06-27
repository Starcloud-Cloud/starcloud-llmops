package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.ocr.OcrResult;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.convert.xhs.material.XhsNoteConvert;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.ocr.AliyunOcrManager;
import com.starcloud.ops.business.app.service.xhs.crawler.impl.XhsDumpServiceImpl;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_OCR_ERROR;

@Slf4j
@TaskComponent
public class XhsParseActionHandler extends BaseActionHandler {

    private static final XhsDumpServiceImpl XHS_DUMP_SERVICE = SpringUtil.getBean(XhsDumpServiceImpl.class);

    private static final AliyunOcrManager ALIYUN_OCR_MANAGER = SpringUtil.getBean(AliyunOcrManager.class);


    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }


    @NoticeVar
    @TaskService(name = "XhsParseActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        // 爬取小红书内容
        String xhsNoteUrl = String.valueOf(params.get(CreativeConstants.XHS_NOTE_URL));
        XhsDetailConstants.validNoteUrl(xhsNoteUrl);
        String noteId = XhsDetailConstants.parsingNoteId(xhsNoteUrl);
        long start = System.currentTimeMillis();
        // 串行转存图片 笔记中有大量图片需改成并行
        ServerRequestInfo noteDetail = XHS_DUMP_SERVICE.requestDetail(noteId);
        long end = System.currentTimeMillis();
        log.info("xhs time {}", end - start);
        XhsNoteDTO xhsNoteDTO = XhsNoteConvert.INSTANCE.convert(noteDetail.getNoteDetail());

        List<OcrGeneralDTO> ocrDTOList = xhsNoteDTO.listOcrDTO();
        // 有大量图片ocr需改成并行
        start = System.currentTimeMillis();
        for (OcrGeneralDTO ocrGeneralDTO : ocrDTOList) {
            OcrResult ocrResult = ALIYUN_OCR_MANAGER.recognizeGeneral(ocrGeneralDTO.getUrl());
            if (!ocrResult.isSuccess()) {
                throw exception(XHS_OCR_ERROR, ocrResult.getMessage());
            }
            BeanUtils.copyProperties(ocrResult.getOcrGeneralDTO(), ocrGeneralDTO);
        }
        end = System.currentTimeMillis();
        log.info("ocr time {}", end - start);
        SseEmitter sseEmitter = context.getSseEmitter();
        if (Objects.nonNull(sseEmitter)) {
            StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
            callBackHandler.onLLMNewToken(JSONUtil.toJsonStr(xhsNoteDTO));
        }
        return convert(xhsNoteDTO, context, ocrDTOList.size());
    }


    private ActionResponse convert(XhsNoteDTO xhsNoteDTO, AppContext context, int cost) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setAnswer(JsonUtils.toJsonString(xhsNoteDTO));
        actionResponse.setOutput(JsonData.of(xhsNoteDTO));
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
