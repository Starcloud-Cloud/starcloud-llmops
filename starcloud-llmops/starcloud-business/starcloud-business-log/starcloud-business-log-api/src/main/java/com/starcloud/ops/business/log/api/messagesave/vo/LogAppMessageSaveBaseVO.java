package com.starcloud.ops.business.log.api.messagesave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

/**
 * 应用执行日志结果保存 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LogAppMessageSaveBaseVO {

    @Schema(description = "uid", required = true, example = "17312")
    @NotNull(message = "uid不能为空")
    private String uid;

    @Schema(description = "会话ID", required = true, example = "15763")
    @NotNull(message = "会话ID不能为空")
    private String appConversationUid;

    @Schema(description = "消息ID", required = true, example = "17481")
    @NotNull(message = "消息ID不能为空")
    private String appMessageUid;

    @Schema(description = "消息内容标识，返回一个结果的情况下字段默认都为空", required = true)
    @NotNull(message = "消息内容标识，返回一个结果的情况下字段默认都为空不能为空")
    private String appMessageItem;

}