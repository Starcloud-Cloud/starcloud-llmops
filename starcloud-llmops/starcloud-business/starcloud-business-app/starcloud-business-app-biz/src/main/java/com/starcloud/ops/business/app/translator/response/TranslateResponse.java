package com.starcloud.ops.business.app.translator.response;

import com.starcloud.ops.business.app.translator.dto.TranslateIndexDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 翻译响应
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class TranslateResponse implements Serializable {

    private static final long serialVersionUID = 5326718839240985886L;

    /**
     * 源语言传入auto时，语种识别后的源语言代码
     */
    public String detectedLanguage;

    /**
     * 翻译后的文本
     */
    public List<TranslateIndexDTO> translatedList;

    /**
     * 翻译后的文本字数
     */
    public Integer wordCount;
}
