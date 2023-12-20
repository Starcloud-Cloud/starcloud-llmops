package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ParagraphDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1337532008546820464L;

    /**
     * 标题
     */
    private String paragraphTitle;

    /**
     * 内容
     */
    private String paragraphContent;

    /**
     * 是否使用
     */
    private Boolean isUseTitle = false;

    /**
     * 是否使用内容
     */
    private Boolean isUseContent = false;

    /**
     * 是否使用
     *
     * @param title   标题
     * @param content 内容
     * @return Section
     */
    public static ParagraphDTO of(String title, String content) {
        ParagraphDTO section = new ParagraphDTO();
        section.setParagraphTitle(title);
        section.setParagraphContent(content);
        section.setIsUseTitle(false);
        section.setIsUseContent(false);
        return section;
    }

}
