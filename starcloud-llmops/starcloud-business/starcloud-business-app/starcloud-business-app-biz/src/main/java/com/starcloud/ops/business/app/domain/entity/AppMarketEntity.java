package com.starcloud.ops.business.app.domain.entity;

import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Data
public class AppMarketEntity {

    /**
     * 应用 UID
     */
    private String uid;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用模型
     */
    private String model;

    /**
     * 应用版本
     */
    private Integer version;

    /**
     * 应用语言
     */
    private String language;

    /**
     * 应用标签
     */
    private List<String> tags;

    /**
     * 应用类别
     */
    private List<String> categories;

    /**
     * 应用场景
     */
    private List<String> scenes;

    /**
     * 应用图片
     */
    private List<String> images;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用是否免费
     */
    private Boolean free;

    /**
     * 应用价格
     */
    private BigDecimal cost;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 查看数量
     */
    private Integer viewCount;

    /**
     * 下载数量
     */
    private Integer downloadCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    private WorkflowConfigEntity workflowConfig;

    /**
     * 应用聊天配置
     */
    private ChatConfigEntity chatConfig;

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


}
