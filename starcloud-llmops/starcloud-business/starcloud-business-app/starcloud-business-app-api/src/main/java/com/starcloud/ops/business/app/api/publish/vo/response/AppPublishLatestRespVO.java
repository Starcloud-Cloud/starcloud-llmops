package com.starcloud.ops.business.app.api.publish.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishRespVO", description = "应用发布响应")
public class AppPublishLatestRespVO implements Serializable {

    private static final long serialVersionUID = 7960398035528250138L;

    /**
     * 发布UID
     */
    @Schema(description = "发布UID")
    private String uid;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @Schema(description = "应用模型")
    private String model;

    /**
     * 发布版本
     */
    @Schema(description = "发布版本")
    private Integer version;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 审核状态（0-未审核，1-审核通过，2-审核未通过，3-用户取消审核）
     */
    @Schema(description = "审核状态")
    private Integer audit;

    /**
     * 审核状态（0-未审核，1-审核通过，2-审核未通过，3-用户取消审核）
     */
    @Schema(description = "审核状态标识，展示用")
    private Integer auditTag;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 应用最后更新时间
     */
    @Schema(description = "应用最后更新时间")
    private LocalDateTime appLastUpdateTime;

    /**
     * 是否需要更新
     */
    @Schema(description = "是否需要更新发布记录，发布按钮是否可用")
    private Boolean needUpdate;

    /**
     * 是否显示发布
     */
    @Schema(description = "是否显示发布，true：显示，false：不显示：即显示 取消发布 按钮")
    private Boolean showPublish;

    /**
     * 是否可以发布
     */
    @Schema(description = "是否可以发布，showPublish 为 true 时，该字段才有意义，true：可以发布，false：不可以发布")
    private Boolean enablePublish;

    /**
     * 是否需要提示
     */
    @Schema(description = "是否需要提示")
    private Boolean needTips;

    /**
     * 是否是第一次发布
     */
    @Schema(description = "是否是第一次生成发布记录")
    private Boolean isFirstCreatePublishRecord;

    /**
     * 发布渠道
     */
    @Schema(description = "发布渠道")
    private Map<Integer, List<AppPublishChannelRespVO>> channelMap;

    /**
     * 应用发布限流信息
     */
    @Schema(description = "应用发布限流信息")
    private AppPublishLimitRespVO limit;

}
