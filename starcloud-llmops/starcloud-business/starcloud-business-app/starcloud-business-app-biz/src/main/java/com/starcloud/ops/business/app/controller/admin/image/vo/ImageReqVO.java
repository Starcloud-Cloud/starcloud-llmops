package com.starcloud.ops.business.app.controller.admin.image.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.api.image.vo.request.BaseImageRequest;
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

/**
 * 图片生成应用请求视图对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-13
 */
@SuppressWarnings("all")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成应用实体")
public class ImageReqVO extends AppContextReqVO {

    private static final long serialVersionUID = 1317039328445443438L;

    /**
     * sse对象
     */
    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    /**
     * 图片生成请求
     */
    @Valid
    @Schema(description = "图片生成请求")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "scene")
    private BaseImageRequest imageRequest;

    /**
     * 图片处理模板
     */
    @Schema(description = "图片处理模板")
    private BaseImageHandler imageHandler;

}
