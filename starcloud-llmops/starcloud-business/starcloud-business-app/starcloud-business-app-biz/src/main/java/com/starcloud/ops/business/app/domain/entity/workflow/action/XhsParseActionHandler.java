package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.api.ocr.OcrResult;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.convert.xhs.material.XhsNoteConvert;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.service.ocr.AliyunOcrManager;
import com.starcloud.ops.business.app.service.xhs.crawler.impl.XhsDumpServiceImpl;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.NO_MATERIAL_DEFINE;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_OCR_ERROR;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_OCR_PARAM_REQUIRED;

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
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();

        // 爬取小红书内容
        String xhsNoteUrl = String.valueOf(params.get(CreativeConstants.XHS_NOTE_URL));
        XhsDetailConstants.validNoteUrl(xhsNoteUrl);

        Object fieldDefine = params.get(CreativeConstants.FIELD_DEFINE);
        Object fieldMap = params.get(CreativeConstants.FIELD_MAP);
        // 参数必填校验 fieldMap校验图片对应的字段类型
        if (Objects.isNull(fieldDefine) || Objects.isNull(fieldMap)) {
            throw exception(XHS_OCR_PARAM_REQUIRED);
        }

        List<MaterialFieldConfigDTO> fieldConfigDTOList = MaterialDefineUtil.parseConfig(JSONUtil.toJsonStr(fieldDefine));
        if (CollUtil.isEmpty(fieldConfigDTOList)) {
            throw exception(NO_MATERIAL_DEFINE);
        }

        String noteId = XhsDetailConstants.parsingNoteId(xhsNoteUrl);
        ServerRequestInfo noteDetail = XHS_DUMP_SERVICE.requestDetail(noteId);

        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
        };
        Map<String, Object> material = XhsNoteConvert.INSTANCE.convert(noteDetail.getNoteDetail(), JSONObject.parseObject(JSONObject.toJSONString(fieldMap), typeReference));

        for (MaterialFieldConfigDTO materialFieldConfigDTO : fieldConfigDTOList) {
            if (!MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(materialFieldConfigDTO.getType())) {
                continue;
            }
            String fieldName = materialFieldConfigDTO.getFieldName();
            Object url = material.get(fieldName);
            if (Objects.isNull(url)) {
                continue;
            }

            OcrResult ocrResult = ALIYUN_OCR_MANAGER.recognizeGeneral(url.toString());
            if (!ocrResult.isSuccess()) {
                throw exception(XHS_OCR_ERROR, ocrResult.getMessage());
            }

            material.put(fieldName + "_ocr", ocrResult.getOcrGeneralDTO());
        }
        return convert(material, context);
    }


    private ActionResponse convert(Map<String, Object> material, AppContext context) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.TRUE);
        actionResponse.setIsShow(Boolean.FALSE);
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setAnswer(material.toString());
        actionResponse.setOutput(JsonData.of(material));
        actionResponse.setMessage(JsonUtils.toJsonString(context.getContextVariablesValues()));
        actionResponse.setStepConfig(context.getContextVariablesValues());
        actionResponse.setMessageTokens(0L);
        actionResponse.setMessageUnitPrice(new BigDecimal("0"));
        actionResponse.setAnswerTokens(0L);
        actionResponse.setAnswerUnitPrice(new BigDecimal("0"));
        actionResponse.setTotalTokens(0L);
        actionResponse.setTotalPrice(new BigDecimal("0"));
        actionResponse.setAiModel(null);
        // 组装消耗为 0
        actionResponse.setCostPoints(0);
        return actionResponse;
    }

}
