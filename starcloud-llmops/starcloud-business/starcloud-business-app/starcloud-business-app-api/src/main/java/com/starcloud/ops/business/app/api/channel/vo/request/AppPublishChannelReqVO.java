package com.starcloud.ops.business.app.api.channel.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import com.starcloud.ops.business.app.enums.publish.AppPublishChannelEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Schema(name = "AppPublishChannelReqVO", description = "应用发布渠道请求")
public class AppPublishChannelReqVO implements Serializable {

    private static final long serialVersionUID = -1428829532085814206L;

    /**
     * 渠道 uid
     */
    @Schema(description = "Uid")
    private String uid;

    /**
     * 应用 Uid
     */
    @Schema(description = "应用 Uid")
    @NotBlank(message = "应用 Uid 不能为空")
    private String appUid;

    /**
     * 发布 Uid
     */
    @Schema(description = "发布 Uid")
    @NotBlank(message = "发布 Uid 不能为空")
    private String publishUid;

    /**
     * 渠道类型
     */
    @Schema(description = "渠道类型")
    @NotNull(message = "渠道类型不能为空")
    @InEnum(value = AppPublishChannelEnum.class, field = InEnum.EnumField.CODE, message = "渠道类型{value}, 必须在 {values} 范围内")
    private Integer type;

    /**
     * 渠道配置
     */
    @Schema(description = "渠道配置")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
    private BaseChannelConfigDTO config;

    /**
     * 渠道状态
     */
    @Schema(description = "渠道状态")
    private Integer status;


}
