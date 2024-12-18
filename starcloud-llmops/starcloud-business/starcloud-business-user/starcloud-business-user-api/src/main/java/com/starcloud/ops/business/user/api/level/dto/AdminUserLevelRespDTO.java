package com.starcloud.ops.business.user.api.level.dto;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员等级 Resp DTO
 *
 * @author 芋道源码
 */
@Data
public class AdminUserLevelRespDTO {

    private Long id;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 等级名称
     */
    private String levelName;
    /**
     * 生效开始时间
     */
    private LocalDateTime validStartTime;
    /**
     * 生效结束时间
     */
    private LocalDateTime validEndTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
