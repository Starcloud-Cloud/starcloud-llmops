package com.starcloud.ops.business.user.dal.dataobject.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员等级 DO
 * <p>
 * 配置每个等级需要的积分
 *
 * @author owen
 */
@TableName("system_user_level")
@KeySequence("system_user_level_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserLevelConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 等级
     */
    private Integer level;

    /**
     *  所关联的角色 Id
     *  {@link RoleDO#getId()} ()} 字段
     */
    private Long roleId;
    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级图标
     */
    private String icon;
    /**
     * 等级配置
     */
    private String levelConfig;
    /**
     * 等级背景图
     */
    private String backgroundUrl;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;


}
