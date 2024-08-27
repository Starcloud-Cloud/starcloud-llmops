package com.starcloud.ops.business.app.controller.admin.app.vo;

import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.Map;

/**
 * 应用类别 DTO 对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "应用执行请求对象")
public class AppExecuteRequest extends AppContextReqVO {

    private static final long serialVersionUID = -5539763604602272162L;

    /**
     * sse对象
     */
    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    /**
     * 应用执行步骤ID
     */
    @Schema(description = "应用执行步骤ID")
    private String uid;

    /**
     * 全局参数
     */
    @Schema(description = "全局参数")
    private Map<String, Object> globalParams;
}
