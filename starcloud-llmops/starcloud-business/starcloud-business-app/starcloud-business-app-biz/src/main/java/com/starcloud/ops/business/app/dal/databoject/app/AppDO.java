package com.starcloud.ops.business.app.dal.databoject.app;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 应用表对应的实体对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_app")
@KeySequence("llm_app_seq")
public class AppDO extends TenantBaseDO {

    private static final long serialVersionUID = 1345563234255L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用唯一 ID
     */
    @TableField("uid")
    private String uid;

    /**
     * 应用名称
     */
    @TableField("name")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @TableField("model")
    private String model;

    /**
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    @TableField("type")
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    @TableField("source")
    private String source;

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
     * 应用发布成功后，应用市场 uid-version
     */
    @TableField("publish_uid")
    private String publishUid;

    /**
     * 应用安装成功后，应用市场 uid-version
     */
    @TableField("install_uid")
    private String installUid;

    /**
     * 最后一次发布到应用市场时间
     */
    @TableField("last_publish")
    private LocalDateTime lastPublish;

    @TableField("example")
    private String example;

}
