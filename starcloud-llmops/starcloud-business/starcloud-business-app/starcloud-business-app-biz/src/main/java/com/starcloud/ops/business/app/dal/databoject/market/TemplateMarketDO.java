package com.starcloud.ops.business.app.dal.databoject.market;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
 * 模版市场表
 * </p>
 *
 * @author admin
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_template_market")
@KeySequence("llm_template__market_seq")
public class TemplateMarketDO extends TenantBaseDO {


    private static final long serialVersionUID = -7392992852206247688L;

    /**
     * 模板市场ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模版市场 key，我的模版上传到模版市场时候，会生成一个模版市场 key，下载模版的时候，会将该 key 存到此处。
     */
    @TableField("uid")
    private String uid;

    /**
     * 模版名称
     */
    @TableField("name")
    private String name;

    /**
     * 模版类型, SYSTEM：系统推荐模版，MY_TEMPLATE：我的模版，DOWNLOAD_TEMPLATE：下载模版
     */
    @TableField("type")
    private String type;

    /**
     * 模版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
     */
    @TableField("logotype")
    private String logotype;

    /**
     * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 模版版本，默认版本 1.0.0
     */
    @TableField("version")
    private String version;

    /**
     * 模版标签，多个以逗号分割
     */
    @TableField("tags")
    private String tags;

    /**
     * 模版类别，多个以逗号分割
     */
    @TableField("categories")
    private String categories;

    /**
     * 模版场景，多个以逗号分割
     */
    @TableField("scenes")
    private String scenes;

    /**
     * 模版语言
     */
    @TableField("language")
    private String language;

    /**
     * 模版详细配置信息, 步骤，变量，场景等
     */
    @TableField("config")
    private String config;

    /**
     * 模版图片，多个以逗号分割
     */
    @TableField("images")
    private String images;

    /**
     * 模版图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 模版步骤图标、多个以逗号分割
     */
    @TableField("step_icons")
    private String stepIcons;

    /**
     * 步骤数量
     */
    @TableField("step_count")
    private Integer stepCount;

    /**
     * 模版描述
     */
    @TableField("description")
    private String description;

    /**
     * 模版 Prompt详情
     */
    @TableField("prompt_info")
    private String promptInfo;

    /**
     * 模版收费数
     */
    @TableField("cost")
    private BigDecimal cost;

    /**
     * 模版 word
     */
    @TableField("word")
    private Integer word;

    /**
     * 模版是否是免费的
     */
    @TableField("free")
    private Boolean free;

    /**
     * 点赞数量
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 查看数量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 下载数量
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 插件版本
     */
    @TableField("plugin_version")
    private String pluginVersion;

    /**
     * 插件级别
     */
    @TableField("plugin_level")
    private String pluginLevel;

    /**
     * 模版审核
     */
    @TableField("audit")
    private Integer audit;

    /**
     * 模版状态，0：启用，1：禁用
     */
    @TableField("status")
    private Integer status;

}
