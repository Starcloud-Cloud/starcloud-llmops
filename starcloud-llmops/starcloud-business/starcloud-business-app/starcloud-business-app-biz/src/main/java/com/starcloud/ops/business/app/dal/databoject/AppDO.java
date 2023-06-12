package com.starcloud.ops.business.app.dal.databoject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 模版表对应的实体对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_template")
@KeySequence("llm_template_seq")
public class AppDO extends TenantBaseDO {

    private static final long serialVersionUID = 1345563234255L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模版唯一标识
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
     * 上传成功后，模版市场 UID
     */
    @TableField("upload_uid")
    private String uploadUid;

    /**
     * 下载成功后，模版市场 UID
     */
    @TableField("download_uid")
    private String downloadUid;

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
     * 模版描述
     */
    @TableField("description")
    private String description;

    /**
     * 模版状态，0：启用，1：禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后一次上传到模版市场时间
     */
    @TableField("last_upload")
    private LocalDateTime lastUpload;

}
