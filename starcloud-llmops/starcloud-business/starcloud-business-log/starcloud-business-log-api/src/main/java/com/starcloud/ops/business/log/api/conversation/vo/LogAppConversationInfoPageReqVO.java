package com.starcloud.ops.business.log.api.conversation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.log.enums.LogQueryTypeEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "LogAppConversationInfoPageReqVO", description = "应用会话日志分页查询请求 VO")
public class LogAppConversationInfoPageReqVO extends PageParam {

    private static final long serialVersionUID = -6444036479357643539L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String appMode;

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
     * 用户
     */
    @Schema(description = "用户")
    private String user;

    /**
     * 终端用户
     */
    @Schema(description = "终端用户")
    private String endUser;

    /**
     * 查询类型
     */
    @Schema(description = "查询类型")
    @NotNull(message = "查询类型不能为空")
    @InEnum(value = LogQueryTypeEnum.class, field = InEnum.EnumField.NAME, message = "查询类型 {value}, 支持的类型为 {values}")
    private String type;

    /**
     * 应用模型列表
     */
    @Schema(hidden = true)
    private List<String> appModeList;

    /**
     * 查询时间范围类型
     */
    @Schema(description = "查询时间范围类型")
    @InEnum(value = LogTimeTypeEnum.class, field = InEnum.EnumField.NAME, message = "查询时间范围类型 {value}, 支持的类型为 {values}")
    private String timeType;

    /**
     * 创建时间
     */
    @Schema(description = "数据开始时间")
    private LocalDateTime startTime;

    /**
     * 创建时间
     */
    @Schema(description = "数据结束时间")
    private LocalDateTime endTime;

}