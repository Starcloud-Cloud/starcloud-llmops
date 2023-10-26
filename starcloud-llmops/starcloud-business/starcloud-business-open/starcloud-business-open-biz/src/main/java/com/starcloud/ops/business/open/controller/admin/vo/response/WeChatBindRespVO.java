package com.starcloud.ops.business.open.controller.admin.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "绑定微信群聊")
public class WeChatBindRespVO {

    @Schema(description = "服务器地址")
    private String url;

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "ip白名单")
    private List<String> whitelist;

    @Schema(description = "加密密钥")
    private String encodingAesKey;

    @Schema(description = "是否加密")
    private Boolean encryption;

    @Schema(description = "公共号Id")
    private Long mpAccountId;
}
