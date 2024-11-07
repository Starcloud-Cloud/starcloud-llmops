package com.starcloud.ops.business.poster.dal.dataobject.material;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.starcloud.ops.business.poster.enums.material.MaterialTypeEnum;
import lombok.*;

import java.util.List;

/**
 * 海报素材 DO
 *
 * @author starcloudadmin
 */
@TableName("poster_material")
@KeySequence("poster_material_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDO extends DeptBaseDO {

    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 分组编号
     */
    private Long groupId;
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
     * {@link MaterialTypeEnum}
     */
    private Integer type;
    /**
     * 标签
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> materialTags;
    /**
     * 素材数据
     */
    private String materialData;
    /**
     * 请求数据
     */
    private String requestParams;
    /**
     * 素材分类编号
     */
    private Long categoryId;
    /**
     * 开启状态
     */
    private Boolean status;
    /**
     * 分类排序
     */
    private Integer sort;
    /**
     * 用户类型
     * <p>
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

}