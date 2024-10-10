package com.starcloud.ops.business.app.dal.databoject.materiallibrary;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import lombok.*;

/**
 * 素材知识库表格信息 DO
 *
 * @author starcloudadmin
 */
@TableName("llm_material_library_table_column")
@KeySequence("llm_material_library_table_column_seq")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialLibraryTableColumnDO extends DeptBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 素材库ID
     */
    private Long libraryId;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列宽
     */
    private Integer columnWidth;
    /**
     * 列名
     */
    private String columnCode;
    /**
     * 类型
     */
    private Integer columnType;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否必须
     */
    private Boolean isRequired;
    /**
     * 序号
     */
    private Long sequence;
    /**
     * 是否是分组字段
     */
    private Boolean isGroupColumn;

}