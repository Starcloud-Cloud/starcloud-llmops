package com.starcloud.ops.business.log.api.conversation.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用会话日志分页查询请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "AppLogConversationPageQuery", description = "应用会话日志分页查询请求 VO")
public class AppLogConversationInfoPageReqVO extends PageParam {

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
     * AI模型
     */
    @Schema(description = "AI模型")
    private String aiModel;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private String fromScene;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    @InEnum(value = LogStatusEnum.class, field = InEnum.EnumField.NAME, message = "应用状态 {value} 不合法，必须在 {values} 中")
    private String status;

    /**
     * 应用场景列表
     */
    @Schema(hidden = true)
    private List<String> fromSceneList;

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