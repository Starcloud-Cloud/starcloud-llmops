package com.starcloud.ops.business.app.controller.admin.comment.vo.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;

/**
 * 微信开放平台 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
@Schema(description = "微信开放平台 BaseVO")
@Valid
public class ChatBotOpenApiDetailRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "所属用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "开放 API 的 token", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String token;

    @Schema(description = "开放 API 的 secret", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String appSecret;

    @Schema(description = "开放 API 的 webhook", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @URL(message = "开放 API 的 webhook 必须是 URL 格式")
    private String webhook;

    @Schema(description = "开放 API 的 webhook 密钥")
    private String webhookSecret;

}
