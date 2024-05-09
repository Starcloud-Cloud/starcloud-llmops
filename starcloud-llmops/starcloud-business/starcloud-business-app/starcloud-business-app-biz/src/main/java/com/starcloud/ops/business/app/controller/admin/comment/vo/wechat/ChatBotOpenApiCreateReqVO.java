package com.starcloud.ops.business.app.controller.admin.comment.vo.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信开放平台 Create VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Schema(description = "微信开放平台 Create Request VO")
@Data
public class ChatBotOpenApiCreateReqVO extends ChatBotOpenApiBaseVO {

}
