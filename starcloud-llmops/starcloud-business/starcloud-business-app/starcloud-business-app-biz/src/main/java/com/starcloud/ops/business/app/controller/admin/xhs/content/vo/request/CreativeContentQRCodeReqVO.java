package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 创作内容二维码请求
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容二维码请求")
public class CreativeContentQRCodeReqVO implements Serializable {

    private static final long serialVersionUID = -4658914833069290703L;

    /**
     * 域名
     */
    @NotBlank(message = "域名不能为空")
    private String domain;

    /**
     * 创作内容UID
     */
    @NotBlank(message = "创作内容UID不能为空")
    private String uid;

    /**
     * 二维码类型
     */
    private String type;

}
