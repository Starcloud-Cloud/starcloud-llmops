package com.starcloud.ops.business.app.translator;

import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;

/**
 * 翻译基本接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
public interface Translator {

    /**
     * 文本翻译
     *
     * @param request 翻译请求
     * @return 翻译结果
     */
    TranslateResponse translate(TranslateRequest request);
}
