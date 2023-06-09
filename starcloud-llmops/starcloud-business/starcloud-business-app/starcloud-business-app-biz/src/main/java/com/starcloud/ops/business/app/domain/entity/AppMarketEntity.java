package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
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
     * 使用数量
     */
    private Integer usageCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 查看数量
     */
    private Integer viewCount;

    /**
     * 安装数量
     */
    private Integer installCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    private WorkflowConfigEntity workflowConfig;

    /**
     * 应用聊天配置
     */
    private ChatConfigEntity chatConfig;

    /**
     * 应用图片配置
     */
    private ImageConfigEntity imageConfig;

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
     * 应用市场数据库操作类
     */
    private static AppMarketRepository appMarketRepository;

    /**
     * 获取应用市场数据库操作类
     *
     * @return 应用市场数据库操作类
     */
    public static AppMarketRepository getAppMarketRepository() {
        if (appMarketRepository == null) {
            appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);
        }
        return appMarketRepository;
    }

    /**
     * 校验
     */
    public void validate() {
        if (AppModelEnum.COMPLETION.name().equals(this.model)) {
            workflowConfig.validate();
        } else if (AppModelEnum.CHAT.name().equals(this.model)) {
            chatConfig.validate();
        }
    }

    /**
     * 新增应用
     */
    public void insert() {
        validate();
        getAppMarketRepository().insert(this);
    }

    /**
     * 更新应用
     */
    public void update() {
        validate();
        getAppMarketRepository().update(this);
    }


}
