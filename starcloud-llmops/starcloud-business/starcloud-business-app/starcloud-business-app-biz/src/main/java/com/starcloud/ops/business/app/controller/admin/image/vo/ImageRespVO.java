package com.starcloud.ops.business.app.controller.admin.image.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.image.vo.response.BaseImageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成应用实体")
public class ImageRespVO implements Serializable {

    private static final long serialVersionUID = -3225734608186743394L;

    /**
     * 会话UID
     */
    @Schema(description = "会话 ID")
    private String conversationUid;

    private String bizUid;

    private String scene;

    /**
     * 图片生成结果
     */
    private BaseImageResponse response;

}
