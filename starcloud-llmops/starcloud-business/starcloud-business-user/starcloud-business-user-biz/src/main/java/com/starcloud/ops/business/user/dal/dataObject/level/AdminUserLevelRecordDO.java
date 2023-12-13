package com.starcloud.ops.business.user.dal.dataobject.level;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员等级记录 DO
 * <p>
 * 用户每次等级发生变更时，记录一条日志
 *
 * @author owen
 */
@TableName("system_user_level_record")
@KeySequence("system_user_level_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserLevelRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     * <p>
     * 关联 {@link AdminUserDO#getId()} 字段
     */
    private Long userId;
    /**
     * 等级编号
     * <p>
     * 关联 {@link AdminUserLevelDO#getId()} 字段
     */
    private Long levelId;
    /**
     * 修改前用户等级
     * <p>
     * 冗余 {@link AdminUserLevelDO#getLevel()} 字段
     */
    private Integer levelBefore;
    /**
     * 修改后用户等级
     */
    private Integer levelAfter;

    /**
     * 生效开始时间
     */
    private LocalDateTime validStartTime;
    /**
     * 生效结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 备注
     */
    private String remark;
    /**
     * 描述
     */
    private String description;


}
