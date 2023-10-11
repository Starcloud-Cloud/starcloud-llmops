package com.starcloud.ops.business.app.api.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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

    @Schema(description = "公众号微信号", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaoyuanma")
    private String account;

    @Schema(description = "公众号密钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "3a7b3b20c537e52e74afd395eb85f61f")
    private String appSecret;

    @Schema(description = "公众号 token", requiredMode = Schema.RequiredMode.REQUIRED, example = "kangdayuzhen")
    private String token;

    @Schema(description = "备注", example = "请关注芋道源码，学习技术")
    private String remark;

    @Schema(description = "公共号数据库Id")
    private Long accountId;

    @Schema(description = "回调地址")
    private String url;

    @Schema(description = "白名单")
    private List<String> whitelist;
}
