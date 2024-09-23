package com.starcloud.ops.business.poster.dal.dataobject.material;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.starcloud.ops.business.poster.enums.material.MaterialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

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
public class MaterialDO extends BaseDO {

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
     * 标题
     */
    private String title;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 描述
     */
    private String introduction;
    /**
     * 类型
     * {@link MaterialTypeEnum}
     */
    private String type;
    /**
     * 标签
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private  List<String> materialTags;
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
    private Integer status;
    /**
     * 分类排序
     */
    private Integer sort;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

}