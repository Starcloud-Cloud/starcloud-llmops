package com.starcloud.ops.business.log.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import lombok.*;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 应用执行日志结果保存 DO
 *
 * @author 芋道源码
 */
@TableName("llm_log_app_message_save")
@KeySequence("llm_log_app_message_save_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAppMessageSaveDO extends DeptBaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * uid
     */
    private String uid;
    /**
     * 会话ID
     */
    private String appConversationUid;
    /**
     * 消息ID
     */
    private String appMessageUid;
    /**
     * 消息内容标识，返回一个结果的情况下字段默认都为空
     */
    private String appMessageItem;

}