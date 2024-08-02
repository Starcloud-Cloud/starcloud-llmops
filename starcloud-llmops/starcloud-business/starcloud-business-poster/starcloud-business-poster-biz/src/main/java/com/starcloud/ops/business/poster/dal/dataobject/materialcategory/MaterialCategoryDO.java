package com.starcloud.ops.business.poster.dal.dataobject.materialcategory;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 素材分类 DO
 *
 * @author starcloudadmin
 */
@TableName("poster_material_category")
@KeySequence("poster_material_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryDO extends BaseDO {

    /**
     * 父分类编号 - 根分类
     */
    public static final Long PARENT_ID_NULL = 0L;
    /**
     * 限定分类层级
     */
    public static final int CATEGORY_LEVEL = 3;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 父分类编号
     */
    private Long parentId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 分类排序
     */
    private Integer sort;

    /**
     * 开启状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}