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
@Schema(name = "WeChatReplyChannelConfigDTO", description = "微信公共号自动回复")
public class WeChatReplyChannelConfigDTO  extends BaseChannelConfigDTO {

}
