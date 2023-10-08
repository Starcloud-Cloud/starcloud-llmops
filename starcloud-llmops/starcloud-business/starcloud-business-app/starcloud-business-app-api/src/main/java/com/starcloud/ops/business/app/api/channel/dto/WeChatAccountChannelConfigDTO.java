package com.starcloud.ops.business.app.api.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "WeChatAccountChannelConfigDTO", description = "微信公共号发布渠道")
public class WeChatAccountChannelConfigDTO extends BaseChannelConfigDTO {

    @Schema(description = "微信企业id")
    private String wxAppId;

    @Schema(description = "公共号名称")
    private String name;

}
