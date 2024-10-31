package com.starcloud.ops.business.log.api.conversation.vo.request;

import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 管理后台 - 应用执行日志会话创建 Request VO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Schema(description = "管理后台 - 应用执行日志会话创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationCreateReqVO extends LogAppConversationBaseVO {

    private static final long serialVersionUID = 7130705377365042499L;

    /**
     * 多租户编号
     */
    private Long tenantId;

    /**
     * 指定创建者
     * 1，游客执行的时候，创建者为 执行应用的 创建者
     */
    private String creator;

    /**
     * 指定更新者
     */
    private String updater;

    /**
     * 部门编号
     */
    private Long deptId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}