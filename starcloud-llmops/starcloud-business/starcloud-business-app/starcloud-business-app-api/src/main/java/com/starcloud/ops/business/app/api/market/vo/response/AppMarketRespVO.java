package com.starcloud.ops.business.app.api.market.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.InstalledRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用市场对象响应实体VO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场对象响应实体VO")
public class AppMarketRespVO implements Serializable {

    private static final long serialVersionUID = 4430780734779852216L;

    /**
     * 市场应用 UID
     */
    @Schema(description = "市场应用 UID")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @Schema(description = "应用模型：CHAT：聊天式应用，COMPLETION：生成式应用")
    private String model;

    /**
     * 应用版本，默认版本 1
     */
    @Schema(description = "应用版本，默认版本 1")
    private Integer version;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    private String language;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用场景，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    @Schema(description = "应用图片，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用是否是免费的
     */
    @Schema(description = "应用是否是免费的")
    private Boolean free;

    /**
     * 应用收费数
     */
    @Schema(description = "应用收费数")
    private BigDecimal cost;

    /**
     * 应用点赞数量
     */
    @Schema(description = "应用点赞数量")
    private Integer likeCount;

    /**
     * 应用查看数量
     */
    @Schema(description = "应用查看数量")
    private Integer viewCount;

    /**
     * 应用安装数量
     */
    @Schema(description = "应用安装数量")
    private Integer installCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息, 步骤，变量，场景等")
    private WorkflowConfigRespVO workflowConfig;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private ChatConfigRespVO chatConfig;

    /**
     * 应用步骤数量
     */
    @Schema(description = "应用步骤数量")
    private Integer stepCount;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用example
     */
    @Schema(description = "应用example")
    private String example;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String creator;

    /**
     * 更新者，
     */
    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "应用安装状态")
    private InstalledRespVO installStatus;

}
