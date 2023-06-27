package com.starcloud.ops.business.log.api.conversation.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 应用执行日志会话创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationCreateReqVO extends LogAppConversationBaseVO {

//    /**
//     * 创建时间
//     */
//    private LocalDateTime createTime;
//
//    /**
//     * 最后更新时间
//     */
//    private LocalDateTime updateTime;

}