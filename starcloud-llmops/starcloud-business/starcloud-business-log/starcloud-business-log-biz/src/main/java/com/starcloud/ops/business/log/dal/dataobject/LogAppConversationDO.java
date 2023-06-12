package com.starcloud.ops.business.log.dal.dataobject;

import lombok.*;


import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

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
public class LogAppConversationDO extends BaseDO {

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
     * app 模式
     */
    private String appMode;
    /**
     * app 配置
     */
    private String appConfig;
    /**
     * 模版状态，0：失败，1：成功
     */
    private Byte status;
    /**
     * 执行场景
     */
    private String fromScene;
    /**
     * 终端用户ID
     */
    private String endUser;

}