package com.starcloud.ops.business.app.translator.handler;

import com.starcloud.ops.business.app.translator.Translator;
import com.starcloud.ops.business.app.translator.TranslatorType;
import com.starcloud.ops.business.app.translator.TranslatorTypeEnum;
import com.starcloud.ops.business.app.translator.client.AliyunTranslatorClient;
import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@TranslatorType(TranslatorTypeEnum.ALI_YUN)
@Component
public class AliyunTranslatorHandler implements Translator {

    @Resource
    private AliyunTranslatorClient aliyunTranslatorClient;

    /**
     * 文本翻译
     *
     * @param request 翻译请求
     * @return 翻译结果
     */
    @Override
    public TranslateResponse translate(TranslateRequest request) {
        return aliyunTranslatorClient.bathTranslate(request);
    }

}
