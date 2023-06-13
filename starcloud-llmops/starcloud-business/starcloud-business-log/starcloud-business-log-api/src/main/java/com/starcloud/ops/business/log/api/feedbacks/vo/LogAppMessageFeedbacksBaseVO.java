package com.starcloud.ops.business.log.api.feedbacks.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

/**
 * 应用执行日志结果反馈 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LogAppMessageFeedbacksBaseVO {

    @Schema(description = "uid", required = true, example = "633")
    @NotNull(message = "uid不能为空")
    private String uid;

    @Schema(description = "会话ID", required = true, example = "5983")
    @NotNull(message = "会话ID不能为空")
    private String appConversationUid;

    @Schema(description = "消息ID", required = true, example = "28830")
    @NotNull(message = "消息ID不能为空")
    private String appMessageUid;

    @Schema(description = "消息内容标识，返回一个结果的情况下字段默认都为空", required = true)
    @NotNull(message = "消息内容标识，返回一个结果的情况下字段默认都为空不能为空")
    private String appMessageItem;

    @Schema(description = "反馈类型，like: 喜欢，dislike:不喜欢", required = true)
    @NotNull(message = "反馈类型，like: 喜欢，dislike:不喜欢不能为空")
    private String rating;

    @Schema(description = "临时用户ID")
    private String endUser;

}