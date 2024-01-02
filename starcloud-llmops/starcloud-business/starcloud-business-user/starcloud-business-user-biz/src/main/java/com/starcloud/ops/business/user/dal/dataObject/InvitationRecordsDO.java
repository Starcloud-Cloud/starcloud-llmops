package com.starcloud.ops.business.user.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 邀请记录 DO
 *
 * @author Alancusack
 */
@TableName("llm_invitation_records")
@KeySequence("llm_invitation_records_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRecordsDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;
    /**
     * 邀请人 ID
     */
    private Long inviterId;
    /**
     * 被邀请人 ID
     */
    private Long inviteeId;
    /**
     * 邀请时间
     */
    private LocalDateTime invitationDate;

}
