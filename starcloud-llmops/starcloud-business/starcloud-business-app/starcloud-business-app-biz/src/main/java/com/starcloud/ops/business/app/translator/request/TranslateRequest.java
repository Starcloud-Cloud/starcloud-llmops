package com.starcloud.ops.business.app.translator.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class TranslateRequest implements Serializable {

    private static final long serialVersionUID = 4219041515905891473L;

    /**
     * 源语言
     */
    private String sourceLanguage;

    /**
     * 目标语言
     */
    private String targetLanguage;

    /**
     * 需要翻译的文本
     */
    private List<String> textList;
}
