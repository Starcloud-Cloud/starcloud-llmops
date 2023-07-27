package com.starcloud.ops.business.log.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import lombok.*;


import com.baomidou.mybatisplus.annotation.*;

/**
 * 应用执行日志会话 DO
 *
 * @author 芋道源码
 */
@TableName("llm_log_app_conversation")
@KeySequence("llm_log_app_conversation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAppConversationDO extends TenantBaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 会话uid
     */
    private String uid;
    /**
     * app uid
     */
    private String appUid;

    /**
     * app name
     */
    private String appName;
    /**
     * app 模式
     */
    private String appMode;
    /**
     * app 配置
     */
    private String appConfig;
    /**
     * 执行状态，error：失败，success：成功
     */
    private String status;
    /**
     * 执行场景
     */
    private String fromScene;
    /**
     * 终端用户ID
     */
    private String endUser;

}