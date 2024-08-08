package com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreativePlanRespVO", description = "执行计划响应")
public class CreativePlanRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -8046050421435541629L;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String uid;

    /**
     * 应用UID
     */
    @Schema(description = "应用市场UID")
    private String appUid;

    /**
     * 应用版本号
     */
    @Schema(description = "应用版本号")
    private Integer version;

    /**
     * 创作计划来源
     */
    @Schema(description = "创作计划来源")
    private String source;

    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    private Integer totalCount;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 创建人
     */
    @Schema(description = "计划创建者")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "计划修改者")
    private String updater;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    private String creatorName;

    private List<Verification> verificationList;

}
