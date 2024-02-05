package com.starcloud.ops.business.app.api.app.dto;

import com.starcloud.ops.business.app.enums.app.AppStepStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "步骤状态信息")
public class AppStepStatusDTO implements java.io.Serializable {

    private static final long serialVersionUID = -6697368145792256539L;

    /**
     * 步骤ID
     */
    @Schema(description = "步骤ID")
    private String stepId;

    /**
     * 处理编码
     */
    @Schema(description = "处理编码")
    private String handlerCode;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 耗时
     */
    @Schema(description = "耗时")
    private Long elapsed;

    /**
     * 状态
     *
     * @see com.starcloud.ops.business.app.enums.app.AppStepStatusEnum
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 错误Code
     */
    @Schema(description = "错误Code")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 初始化一个基本的状态信息
     *
     * @param stepId      步骤ID
     * @param handlerCode 步骤处理Code
     * @return AppStepStatusDTO
     */
    public static AppStepStatusDTO initOf(String stepId, String handlerCode) {
        AppStepStatusDTO appStepStatus = new AppStepStatusDTO();
        appStepStatus.setStepId(stepId);
        appStepStatus.setHandlerCode(handlerCode);
        appStepStatus.setStatus(AppStepStatusEnum.WAITING.name());
        return appStepStatus;
    }

}
