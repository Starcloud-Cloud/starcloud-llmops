package com.starcloud.ops.business.app.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class AppExecuteReqVO implements Serializable {

    @Schema(description = "执行会话ID")
    String conversationUid;

    /**
     * 应用类别编号
     */
    @Schema(description = "应用uid")
    String appUid;


    @Schema(description = "应用执行步骤ID")
    String stepId;


    @Schema(description = "应用参数")
    AppReqVO appReqVO;

}
