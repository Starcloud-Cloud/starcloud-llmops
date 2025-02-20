package com.starcloud.ops.business.app.dal.databoject.market;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanMaterialDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用市场表
 * </p>
 *
 * @author admin
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_app_market", autoResultMap = true)
@KeySequence("llm_app_market_seq")
public class AppMarketDO extends TenantBaseDO {

    private static final long serialVersionUID = -7392992852206247688L;

    /**
     * 模板市场ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 市场应用 UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 应用名称
     */
    @TableField("name")
    private String name;

    /**
     * 应用类型：SYSTEM：系统应用，MARKET：市场应用
     */
    @TableField("type")
    private String type;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @TableField("model")
    private String model;

    /**
     * 应用版本，默认版本 1
     */
    @TableField("version")
    private Integer version;

    /**
     * 应用语言
     */
    @TableField("language")
    private String language;

    /**
     * 应用排序，越小越靠前
     */
    @TableField("sort")
    private Long sort;

    /**
     * 应用标签，多个以逗号分割
     */
    @TableField("tags")
    private String tags;

    /**
     * 应用类别，多个以逗号分割
     */
    @TableField("category")
    private String category;

    /**
     * 应用场景，多个以逗号分割
     */
    @TableField("scenes")
    private String scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    @TableField("images")
    private String images;

    /**
     * 应用图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 应用是否是免费的
     */
    @TableField("free")
    private Boolean free;

    /**
     * 应用收费数
     */
    @TableField("cost")
    private BigDecimal cost;

    /**
     * 应用使用数量
     */
    @TableField("usage_count")
    private Integer usageCount;

    /**
     * 应用点赞数量
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 应用查看数量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 应用安装数量
     */
    @TableField("install_count")
    private Integer installCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @TableField("config")
    private String config;

    /**
     * 应用描述
     */
    @TableField("description")
    private String description;

    /**
     * 应用example
     */
    @TableField("example")
    private String example;

    /**
     * 演示
     */
    @TableField("demo")
    private String demo;

    /**
     * 插件列表
     */
    @TableField("plugin_list")
    private String pluginList;

    /**
     * 开启视频生成
     */
    private Boolean openVideoMode;

    /**
     * 应用审核
     */
    @TableField("audit")
    private Integer audit;

    /**
     * 素材列表
     */
    @TableField(typeHandler = CreativePlanMaterialDO.MaterialHandler.class)
    private List<Map<String, Object>> materialList;

    @TableField("styles")
    private String styles;
}
