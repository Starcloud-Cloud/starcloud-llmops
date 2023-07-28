package com.starcloud.ops.business.share.controller.app.vo;

import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AppReq implements Serializable {

    @Schema(description = "场景")
    @NotNull(message = "场景Code 不能为空")
    private String scene;

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID 不能为空")
    private String appUid;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "游客的唯一标识")
    private String endUser;

    @Schema(description = "会话id")
    private String conversationUid;

    @Schema(description = "消息ID")
    private String messageUid;


    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    @Schema(description = "应用执行步骤ID")
    String stepId;

    @Schema(description = "应用参数")
    AppReqVO appReqVO;
    /**
     * jsonSchemas 格式的数据，后面会使用这种方式传递参数
     */
    @Schema(description = "入参")
    private JsonData jsonData;

}
