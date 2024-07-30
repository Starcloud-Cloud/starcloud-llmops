package com.starcloud.ops.business.app.feign.dto.coze;

import lombok.Data;

/**
 * 多模态消息内容。
 *
 * @author nacoyer
 */
@Data
public class CozeObjectString {

    /**
     * 多模态消息内容类型，支持设置为：
     * text：文本类型。
     * file：文件类型。
     * image：图片类型。
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