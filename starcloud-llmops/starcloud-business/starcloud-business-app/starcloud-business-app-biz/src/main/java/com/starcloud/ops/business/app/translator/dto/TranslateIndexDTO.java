package com.starcloud.ops.business.app.translator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class TranslateIndexDTO implements Serializable {

    private static final long serialVersionUID = 6674360761930482338L;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 翻译之后的文本
     */
    private String translated;

    /**
     * 翻译之后的文本长度
     */
    private Integer wordCount;

    public static TranslateIndexDTO of(Integer index, String translated, Integer wordCount) {
        TranslateIndexDTO translateIndexDTO = new TranslateIndexDTO();
        translateIndexDTO.setIndex(index);
        translateIndexDTO.setTranslated(translated);
        translateIndexDTO.setWordCount(wordCount);
        return translateIndexDTO;
    }
}
