package com.starcloud.ops.biz.dal.dataobject.template;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 海报模板 DO
 *
 * @author xhsadmin
 */
@TableName("poster_template")
@KeySequence("poster_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * uid
     */
    private String uid;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 标签
     */
    private String label;
    /**
     * 详情
     */
    private String json;
    /**
     * url
     */
    private String tempUrl;
    /**
     * 顺序
     */
    @TableField(value = "`order`")
    private Integer order;
    /**
     * 类型id
     */
    private String templateTypeUid;
    /**
     * 参数
     */
    private String params;

}