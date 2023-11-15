package com.starcloud.ops.business.app.dal.databoject.scheme;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_creative_scheme")
@KeySequence("llm_creative_scheme_seq")
public class CreativeSchemeDO extends TenantBaseDO {

    private static final long serialVersionUID = 5478900681576226966L;

    /**
     * 创作方案ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创作方案UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 创作方案名称
     */
    @TableField("name")
    private String name;

    /**
     * 创作方案类型
     */
    @TableField("type")
    private String type;

    /**
     * 创作方案类目
     */
    @TableField("category")
    private String category;

    /**
     * 创作方案标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 创作方案描述
     */
    @TableField("description")
    private String description;

    /**
     * 创作方案参考
     */
    @TableField("refers")
    private String refers;

    /**
     * 创作方案配置信息
     */
    @TableField("configuration")
    private String configuration;

    /**
     * 创作方案文案示例
     */
    @TableField("copy_writing_example")
    private String copyWritingExample;

    /**
     * 创作方案图片示例
     */
    @TableField("image_example")
    private String imageExample;

}
