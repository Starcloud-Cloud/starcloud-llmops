package com.starcloud.ops.business.app.translator.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "目标语言不能为空")
    private String targetLanguage;

    /**
     * 需要翻译的文本
     */
    @NotEmpty(message = "翻译文本不能为空")
    private List<String> textList;
}
