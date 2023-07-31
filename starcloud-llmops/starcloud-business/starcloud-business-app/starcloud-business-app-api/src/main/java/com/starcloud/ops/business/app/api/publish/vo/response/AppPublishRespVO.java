package com.starcloud.ops.business.app.api.publish.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class AppPublishRespVO implements Serializable {

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
     * 应用市场 UID
     */
    @Schema(description = "应用市场 UID")
    private String marketUid;

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
     * 应用类别
     */
    @Schema(description = "应用类别")
    private List<String> categories;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    private String language;

    /**
     * 发布的应用数据，一条应用的完整数据。备份，分享链接，发布应用数据，均使用该数据。
     */
    @Schema(description = "发布的应用数据")
    private AppRespVO appInfo;

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
     * 是否是第一次发布
     */
    @Schema(description = "是否是第一次生成发布记录")
    private Boolean isFirstCreatePublishRecord;

}
