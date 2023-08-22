package com.starcloud.ops.business.app.api.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "WecomGroupChannelConfigDTO", description = "企业微信群发布渠道")
public class WecomGroupChannelConfigDTO extends BaseChannelConfigDTO{

    @Schema(description = "群名称")
    private String groupName;

    @Schema(description = "群备注 使用uid")
    private String groupRemark;

    @Schema(description = "群二维码链接")
    private String qrCode;

    @Schema(description = "work-tool 机器人Id")
    private String robotId;


}
