package com.starcloud.ops.business.app.dal.databoject.favorite;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应用操作管理表DO，operate 表示操作类型，LIKE 标识喜欢，VIEW 标识查看，DOWNLOAD 标识下载
 *
 * @author admin
 * @since 2023-06-12
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppFavoritePO implements Serializable {

    private static final long serialVersionUID = -5991599861845320973L;

    /**
     * 市场应用 UID
     */
    private String uid;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用类型：SYSTEM：系统应用，MARKET：市场应用
     */
    private String type;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    private String model;

    /**
     * 应用版本，默认版本 1
     */
    private Integer version;

    /**
     * 应用语言
     */
    private String language;

    /**
     * 应用排序，越小越靠前
     */
    private Long sort;

    /**
     * 应用标签，多个以逗号分割
     */
    private String tags;

    /**
     * 应用类别，多个以逗号分割
     */
    private String category;

    /**
     * 应用场景，多个以逗号分割
     */
    private String scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    private String images;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用是否是免费的
     */
    private Boolean free;

    /**
     * 应用收费数
     */
    private BigDecimal cost;

    /**
     * 使用数量
     */
    private Integer usageCount;

    /**
     * 应用点赞数量
     */
    private Integer likeCount;

    /**
     * 应用查看数量
     */
    private Integer viewCount;

    /**
     * 应用安装数量
     */
    private Integer installCount;

    /**
     * 应用配置
     */
    private String config;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用example
     */
    private String example;

    /**
     * 应用创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 收藏UID
     */
    private String favoriteUid;

    /**
     * 收藏者
     */
    private String favoriteCreator;

    /**
     * 应用收藏时间
     */
    private LocalDateTime favoriteTime;

    private String favoriteType;

    private String styleUid;
}
