package com.starcloud.ops.business.user.dal.dataobject.signin;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 签到规则 DO
 *
 * @author QingX
 */
@TableName("system_user_sign_in_config")
@KeySequence("system_user_sign_in_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserSignInConfigDO extends BaseDO {

    /**
     * 规则自增主键
     */
    @TableId
    private Long id;
    /**
     * 签到第 x 天
     */
    private Integer day;
    /**
     * 奖励积分
     */
    private Integer magicBean;
    /**
     * 奖励经验
     */
    private Integer magicImage;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
