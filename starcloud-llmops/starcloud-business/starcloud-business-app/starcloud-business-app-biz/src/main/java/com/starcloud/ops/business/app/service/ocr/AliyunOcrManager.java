package com.starcloud.ops.business.app.service.ocr;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralResponse;
import com.aliyun.tea.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.ocr.OcrResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_OCR_ERROR;

@Slf4j
@Component
public class AliyunOcrManager {

    @Resource
    private Client ocrClient;


    public OcrResult recognizeGeneral(String url) {

        RecognizeAllTextRequest recognizeAllTextRequest = new RecognizeAllTextRequest();
        recognizeAllTextRequest.setType("Advanced");
        recognizeAllTextRequest.setUrl(url);

        RuntimeOptions runtime = new RuntimeOptions();
        try {

            RecognizeAllTextResponse recognizeAllTextResponse = ocrClient.recognizeAllTextWithOptions(recognizeAllTextRequest, runtime);
            OcrResult ocrResult = new OcrResult();
            if (recognizeAllTextResponse.getStatusCode() != 200) {
                throw exception(XHS_OCR_ERROR, recognizeAllTextResponse.getStatusCode());
            }

            if (StringUtils.isNotBlank(recognizeAllTextResponse.getBody().getMessage())) {
                ocrResult.setSuccess(false);
                ocrResult.setMessage(recognizeAllTextResponse.getBody().getMessage());
                ocrResult.setRequestId(recognizeAllTextResponse.getBody().getRequestId());
                return ocrResult;
            }

            OcrGeneralDTO result = new OcrGeneralDTO();
            result.setContent(recognizeAllTextResponse.getBody().getData().getContent());
            result.setData(JsonUtils.toJsonString(recognizeAllTextResponse.getBody().getData()));
            result.setUrl(url);
            ocrResult.setSuccess(true);
            ocrResult.setOcrGeneralDTO(result);
            return ocrResult;
        } catch (Exception e) {
            log.warn("Recognize General error", e);
            OcrResult ocrResult = new OcrResult();
            ocrResult.setSuccess(false);
            ocrResult.setMessage(e.getMessage());
            return ocrResult;
        }
    }
}
