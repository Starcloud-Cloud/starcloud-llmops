package com.starcloud.ops.business.app.api.channel.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
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
@Schema(name = "AppPublishChannelStatusReqVO", description = "应用发布渠道状态修改请求")
public class AppPublishChannelStatusReqVO implements Serializable {

    private static final long serialVersionUID = 4483656710807092993L;

    /**
     * 渠道 uid
     */
    @Schema(description = "uid")
    @NotBlank(message = "发布渠道 uid 不能为空")
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
     * 渠道状态
     */
    @Schema(description = "渠道状态")
    @NotNull(message = "渠道状态不能为空")
    @InEnum(value = StateEnum.class, field = InEnum.EnumField.CODE, message = "渠道状态{value}, 必须在 {values} 范围内")
    private Integer status;

    /**
     * 渠道描述
     */
    @Schema(description = "渠道描述")
    private String description;


}
