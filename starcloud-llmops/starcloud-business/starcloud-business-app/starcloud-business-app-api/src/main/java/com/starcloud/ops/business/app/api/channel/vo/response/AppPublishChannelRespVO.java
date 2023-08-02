package com.starcloud.ops.business.app.api.channel.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishChannelReqVO", description = "应用发布渠道请求")
public class AppPublishChannelRespVO implements Serializable {

    private static final long serialVersionUID = -1428829532085814206L;

    /**
     * 渠道 Uid
     */
    @Schema(description = "渠道 Uid")
    private String uid;

    /**
     * 应用 Uid
     */
    @Schema(description = "应用 Uid")
    private String appUid;

    /**
     * 发布 Uid
     */
    @Schema(description = "发布 Uid")
    private String publishUid;

    /**
     * 渠道类型
     */
    @Schema(description = "渠道类型")
    private Integer type;

    /**
     * 渠道配置
     */
    @Schema(description = "渠道配置")
    private BaseChannelConfigDTO config;

    /**
     * 渠道状态
     */
    @Schema(description = "渠道状态")
    private Integer status;

    /**
     * 渠道描述
     */
    @Schema(description = "渠道描述")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
