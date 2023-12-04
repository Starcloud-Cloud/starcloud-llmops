package com.starcloud.ops.business.app.controller.admin.xhs.scheme.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeSchemeSseReqVO extends CreativeSchemeReqVO {

    private static final long serialVersionUID = -4442210587034936965L;

    /**
     * SSE
     */
    @Schema(description = "SSE")
    @JsonIgnore
    @JSONField(serialize = false)
    private SseEmitter sseEmitter;

}
