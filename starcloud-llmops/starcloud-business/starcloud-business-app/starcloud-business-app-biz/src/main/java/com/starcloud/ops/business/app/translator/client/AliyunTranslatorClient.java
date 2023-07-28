package com.starcloud.ops.business.app.translator.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.GetBatchTranslateRequest;
import com.aliyun.alimt20181012.models.GetBatchTranslateResponse;
import com.aliyun.alimt20181012.models.GetBatchTranslateResponseBody;
import com.starcloud.ops.business.app.translator.dto.TranslateIndexDTO;
import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Slf4j
@Component
public class AliyunTranslatorClient {

    @Resource
    private Client mtClient;

    /**
     * 批量文本翻译
     *
     * @param request 翻译请求
     * @return 翻译响应
     */
    public TranslateResponse bathTranslate(TranslateRequest request) {

        try {
            GetBatchTranslateRequest bathRequest = new GetBatchTranslateRequest();
            bathRequest.setFormatType("text");
            bathRequest.setApiType("translate_standard");
            bathRequest.setScene("general");
            String sourceLanguage = request.getSourceLanguage();
            if (StringUtils.isBlank(sourceLanguage)) {
                sourceLanguage = "auto";
            }
            bathRequest.setSourceLanguage(sourceLanguage);
            bathRequest.setTargetLanguage(request.getTargetLanguage());
            List<String> textList = request.getTextList();
            if (CollectionUtil.isEmpty(textList)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本不能为空"));
            }
            if (textList.size() > 50) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本不能超过50条"));
            }
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < textList.size(); i++) {
                if (StringUtils.isNotBlank(textList.get(i))) {
                    jsonObject.set(String.valueOf(i), textList.get(i));
                }
            }
            bathRequest.setSourceText(jsonObject.toString());
            GetBatchTranslateResponse bathResponse = mtClient.getBatchTranslate(bathRequest);
            if (bathResponse == null) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本失败"));
            }
            if (bathResponse.getBody() == null) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本失败"));
            }
            if (bathResponse.getStatusCode() != 200) {
                throw ServiceExceptionUtil.exception(Integer.valueOf(new ErrorCode(bathResponse.getStatusCode(), "批量翻译文本失败: RequestId: " + bathResponse.getBody().requestId) + ". You can Go to https://next.api.aliyun.com/troubleshoot?spm=api-workbench.api_explorer.0.0.1694e014X6pJvQ to check your error. Message: " + bathResponse.getBody().message));
            }
            GetBatchTranslateResponseBody body = bathResponse.getBody();
            if (body.getCode() != 200 || CollectionUtil.isEmpty(body.getTranslatedList())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(body.getCode(), body.getMessage() + " RequestId: " + body.requestId + ". You can Go to https://next.api.aliyun.com/troubleshoot?spm=api-workbench.api_explorer.0.0.1694e014X6pJvQ to check your error."));
            }
            String detectedLanguage = "";
            int wordCount = 0;
            List<TranslateIndexDTO> indexList = new ArrayList<>();
            List<Map<String, ?>> translatedList = body.getTranslatedList();
            for (Map<String, ?> item : translatedList) {
                if (item != null && StringUtils.equals((String) item.get("code"), "200")) {
                    String translated = (String) item.get("translated");
                    String index = (String) item.get("index");
                    String count = (String) item.get("wordCount");
                    if (StringUtils.isNotBlank(translated)) {
                        detectedLanguage = (String) item.get("detectedLanguage");
                        wordCount += Integer.parseInt(count);
                        indexList.add(TranslateIndexDTO.of(Integer.parseInt(index), translated, Integer.parseInt(count)));
                    }
                }
            }
            TranslateResponse response = new TranslateResponse();
            response.setDetectedLanguage(detectedLanguage);
            response.setWordCount(wordCount);
            response.setTranslatedList(indexList);
            return response;
        } catch (SerialException e) {
            log.error("批量文本翻译失败 {}", e.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("批量文本翻译失败 {}", e.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(19999, e.getMessage()));
        }
    }
}
