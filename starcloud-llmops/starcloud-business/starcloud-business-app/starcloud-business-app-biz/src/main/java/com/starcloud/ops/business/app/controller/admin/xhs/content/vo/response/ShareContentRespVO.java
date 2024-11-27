package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ShareContentRespVO implements Serializable {

    private static final long serialVersionUID = -4077497124089403221L;

    private String appName;

    private String planUid;

    private String batchUid;

    /**
     * 总数
     */
    @Schema(description = "总数")
    private Integer totalCount;

    /**
     * 失败数
     */
    @Schema(description = "失败数")
    private Integer failureCount;

    /**
     * 成功数
     */
    @Schema(description = "成功数")
    private Integer successCount;

    /**
     * 开始执行时间
     */
    @Schema(description = "开始执行时间")
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    @Schema(description = "执行结束时间")
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 批次状态，执行中，执行结束 {@link CreativePlanStatusEnum}
     */
    @Schema(description = "批次状态")
    private String status;

    /**
     * 批次创建者
     */
    @Schema(description = "批次创建者")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    List<CreativeContentRespVO> contentList;
}
