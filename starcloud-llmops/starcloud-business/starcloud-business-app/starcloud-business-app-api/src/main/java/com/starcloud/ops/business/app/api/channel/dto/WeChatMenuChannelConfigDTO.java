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
@Schema(name = "WeChatMenuChannelConfigDTO", description = "微信公共号菜单")
public class WeChatMenuChannelConfigDTO extends BaseChannelConfigDTO{
}
