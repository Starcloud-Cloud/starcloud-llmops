package com.starcloud.ops.business.app.controller.admin.image.vo.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TaskImageVO", description = "图片信息")
public class TaskImageVO implements Serializable {

    private static final long serialVersionUID = -1350771831469250826L;

    /**
     * 图片的唯一标识
     */
    @Schema(description = "图片的唯一标识")
    private String uuid;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    private String url;

    /**
     * 图片媒体类型
     */
    @Schema(description = "图片媒体类型")
    private String mediaType;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;


}
