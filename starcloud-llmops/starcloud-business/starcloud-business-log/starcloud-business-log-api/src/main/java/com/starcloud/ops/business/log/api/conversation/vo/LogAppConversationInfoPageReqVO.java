package com.starcloud.ops.business.log.api.conversation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 应用执行日志会话 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationInfoPageReqVO extends PageParam {

    @Schema(description = "会话uid", required = true, example = "10286")
    private String uid;

    @Schema(description = "app uid", required = true, example = "24921")
    private String appUid;

    @Schema(description = "app 模式", required = true)
    private String appMode;

    @Schema(description = "app 名称", required = true)
    private String appName;


    @Schema(description = "执行场景", required = true)
    private String fromScene;


    @Schema(description = "执行总耗时", required = true)
    private Long elapsed;


    @Schema(description = "执行状态，error：失败，success：成功", required = true, example = "2")
    private String status;

    @Schema(description = "注册用户ID")
    private String creator;

    @Schema(description = "终端用户ID")
    private String endUser;


    private LocalDateTime startTime;


    private LocalDateTime endTime;

}