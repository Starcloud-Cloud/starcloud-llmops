package com.starcloud.ops.business.user.dal.dataobject.tag;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 会员标签 DO
 *
 * @author 芋道源码
 */
@TableName(value = "system_user_tag", autoResultMap = true)
@KeySequence("system_user_tag_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserTagDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;


    /**
     * 会员标签列表，以逗号分隔
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> tagIds;

}
