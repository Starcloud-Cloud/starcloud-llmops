package com.starcloud.ops.business.open.controller.admin.vo.request;

import com.starcloud.ops.framework.common.api.enums.StateEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WechatWebChannelReqVO {

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
     * 发布渠道名称
     */
    @Schema(description = "发布渠道名称")
    @NotBlank(message = "发布渠道名称不能为空")
    private String name;

    /**
     * 渠道状态
     */
    @Schema(description = "渠道状态")
    @InEnum(value = StateEnum.class, field = InEnum.EnumField.CODE, message = "渠道状态{value}, 必须在 {values} 范围内")
    private Integer status;
}
