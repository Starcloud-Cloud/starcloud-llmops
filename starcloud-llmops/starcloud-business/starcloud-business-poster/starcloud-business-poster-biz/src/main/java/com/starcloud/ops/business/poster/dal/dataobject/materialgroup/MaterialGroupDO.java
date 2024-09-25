package com.starcloud.ops.business.poster.dal.dataobject.materialgroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 海报素材分组 DO
 *
 * @author starcloudadmin
 */
@TableName("poster_material_group")
@KeySequence("poster_material_group_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialGroupDO extends BaseDO {

    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String uid;
    /**
     * 名称
     */
    private String name;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 分类编号
     */
    private Long categoryId;
    /**
     * 标签
     */
    private String materialTags;
    /**
     * 开启状态
     */
    private Boolean status;
    /**
     * 公开状态
     */
    private Boolean overtStatus;
    /**
     * 关联编号
     */
    private Long associatedId;
    /**
     * 用户类型
     */
    private Integer userType;

}