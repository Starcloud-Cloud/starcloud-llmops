package com.starcloud.ops.business.app.api.channel.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishChannelModifyReqVO", description = "应用发布渠道修改请求")
public class AppPublishChannelModifyReqVO implements Serializable {

    private static final long serialVersionUID = -1090773335148582202L;

    /**
     * 渠道 uid
     */
    @Schema(description = "uid")
    @NotBlank(message = "发布渠道 uid 不能为空")
    private String uid;

    /**
     * 发布渠道名称
     */
    @Schema(description = "发布渠道名称")
    @NotBlank(message = "发布渠道名称不能为空")
    private String name;

    /**
     * 渠道配置
     */
    @Schema(description = "渠道配置")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
    private BaseChannelConfigDTO config;

    /**
     * 渠道描述
     */
    @Schema(description = "渠道描述")
    private String description;

}
