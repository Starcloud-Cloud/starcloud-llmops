package com.starcloud.ops.business.log.api.conversation.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "LogAppConversationInfoPageAppUidReqVO", description = "应用会话日志分页查询请求 VO")
public class LogAppConversationInfoPageAppUidReqVO extends PageParam {

    private static final long serialVersionUID = -8014401115048627071L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 应用市场 UID
     */
    @Schema(description = "应用市场 UID", hidden = true)
    private String marketUid;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private String fromScene;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 查询时间范围类型
     */
    @Schema(description = "查询时间范围类型")
    @InEnum(value = LogTimeTypeEnum.class, field = InEnum.EnumField.NAME, message = "查询时间范围类型 {value}, 支持的类型为 {values}")
    private String timeType;

    /**
     * 创建时间
     */
    @Schema(description = "数据开始时间", hidden = true)
    private LocalDateTime startTime;

    /**
     * 创建时间
     */
    @Schema(description = "数据结束时间", hidden = true)
    private LocalDateTime endTime;


}