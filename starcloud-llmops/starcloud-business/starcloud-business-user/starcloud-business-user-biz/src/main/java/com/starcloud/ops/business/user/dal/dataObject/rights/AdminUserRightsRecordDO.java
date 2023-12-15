package com.starcloud.ops.business.user.dal.dataobject.rights;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.*;

/**
 * 用户积分记录 DO
 *
 * @author QingX
 */
@TableName("system_user_rights_record")
@KeySequence("system_user_rights_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRightsRecordDO extends BaseDO {

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
     * 积分标题
     */
    private String title;
    /**
     * 积分描述
     */
    private String description;
    /**
     * 权益类型
     * 枚举 {@link AdminUserRightsTypeEnum}
     */
    private Integer rightsType;
    /**
     * 变动权益
     * <p>
     * 1、正数表示获得积分
     * 2、负数表示消耗积分
     */
    private Integer rightsAmount;


}
