package com.starcloud.ops.business.app.util;

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
public class Paragraph implements java.io.Serializable {

    private static final long serialVersionUID = -1337532008546820464L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

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
    public static Paragraph of(String title, String content) {
        Paragraph section = new Paragraph();
        section.setTitle(title);
        section.setContent(content);
        section.setIsUseTitle(false);
        section.setIsUseContent(false);
        return section;
    }

}
