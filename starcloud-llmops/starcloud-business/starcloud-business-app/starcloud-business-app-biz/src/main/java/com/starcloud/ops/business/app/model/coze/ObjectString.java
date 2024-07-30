package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ObjectString implements java.io.Serializable {

    private static final long serialVersionUID = -3551282108086870536L;

    /**
     * 多模态消息内容类型，支持设置为：
     * <ol>
     *     <li>text：文本类型。</li>
     *     <li>file：文件类型。</li>
     *     <li>image：图片类型。</li>
     * </ol>
     */
    private String type;

    /**
     * 文本内容。在 type 为 text 时必选。
     */
    private String text;

    /**
     * 文件或图片内容的 ID。
     */
    private String fileId;

    /**
     * 文件或图片内容的 URL。
     */
    private String fileUrl;
}
