package com.starcloud.ops.business.app.dal.databoject.materiallibrary;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import lombok.*;

/**
 * 素材知识库 DO
 *
 * @author starcloudadmin
 */
@TableName("llm_material_library")
@KeySequence("llm_material_library_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialLibraryDO extends DeptBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    private String uid;
    /**
     * 名称
     */
    private String name;
    /**
     * 图标链接
     */
    private String iconUrl;
    /**
     * 描述
     */
    private String description;
    /**
     * 素材类型
     */
    private Integer formatType;

    private Integer libraryType;

    /**
     * 素材库创建来源
     */
    private Integer createSource;

    /**
     * 素材库大小
     */
    private Long allFileSize;

    /**
     * 素材库大小
     */
    private Long fileCount;

    /**
     * 分享范围
     */
    private String shareRange;
    /**
     * 总使用次数
     */
    private Long totalUsedCount;


    /**
     * 插件配置
     */
    private String pluginConfig;
    /**
     * 状态
     */
    private Boolean status;

}