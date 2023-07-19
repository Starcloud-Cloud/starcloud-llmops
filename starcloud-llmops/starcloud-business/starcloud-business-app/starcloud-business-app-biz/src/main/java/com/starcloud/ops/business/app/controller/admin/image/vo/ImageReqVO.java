package com.starcloud.ops.business.app.controller.admin.image.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
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
public class ImageReqVO extends AppContextReqVO {

    private static final long serialVersionUID = 1317039328445443438L;


    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 游客的唯一标识
     */
    @Schema(description = "游客的唯一标识")
    private String endUser;

    /**
     * 图片生成参数配置
     */
    @Valid
    private ImageRequest imageRequest;

}
