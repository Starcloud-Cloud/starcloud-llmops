package com.starcloud.ops.business.user.dal.dataobject.rights;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户积分记录 DO
 *
 * @author QingX
 */
@TableName("system_user_rights")
@KeySequence("system_user_rights_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRightsDO extends BaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     * <p>
     * 对应 MemberUserDO 的 id 属性
     */
    private Long userId;

    /**
     * 业务编码
     */
    private String bizId;
    /**
     * 业务类型
     * <p>
     * 枚举 {@link AdminUserRightsBizTypeEnum}
     */
    private Integer bizType;


    /**
     * 权益标题
     */
    private String title;
    /**
     * 权益描述
     */
    private String description;

    /**
     * 魔法豆
     */
    private Integer magicBean;
    /**
     * 图片值
     */
    private Integer magicImage;


    /**
     * 魔法豆初始值
     */
    private Integer magicBeanInit;
    /**
     * 图片初始值
     */
    private Integer magicImageInit;


    /**
     * '关联用户等级ID'
     * <p>
     */
    private Long userLevelId;

    /**
     * '生效开始时间'
     */
    private LocalDateTime validStartTime;
    /**
     * '生效结束时间'
     */
    private LocalDateTime validEndTime;


    /**
     * 权益状态
     * <p>
     */
    private Integer status;


}
