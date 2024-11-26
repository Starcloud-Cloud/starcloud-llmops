package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 创作内容二维码响应
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容二维码响应")
public class CreativeContentQRCodeRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -8914368730327337313L;

    /**
     * 创作内容UID
     */
    private String uid;

    /**
     * 计划批次UID
     */
    private String batchId;

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 二维码Base64
     */
    private String qrCode;
}
