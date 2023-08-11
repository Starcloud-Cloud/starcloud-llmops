package com.starcloud.ops.business.app.controller.admin.app.vo;

import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
public class AppExecuteReqVO extends AppContextReqVO {

    private static final long serialVersionUID = -7732800112596296391L;

    /**
     * sse对象
     */
    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    /**
     * 应用执行步骤ID
     */
    @Schema(description = "应用执行步骤ID")
    private String stepId;

    /**
     * 应用参数
     */
    @Schema(description = "应用参数")
    private AppReqVO appReqVO;

    /**
     * jsonSchemas 格式的数据，后面会使用这种方式传递参数
     */
    @Schema(description = "入参")
    private JsonData jsonData;


}
