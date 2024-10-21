package com.starcloud.ops.business.log.api.message.vo.request;

import com.starcloud.ops.business.log.api.message.vo.LogAppMessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "管理后台 - 应用执行日志结果创建 Request VO")
public class LogAppMessageCreateReqVO extends LogAppMessageBaseVO {

    private static final long serialVersionUID = 4376830573417239130L;

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
     * 最后更新时间
     */
    private LocalDateTime updateTime;
    
}