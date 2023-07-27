package com.starcloud.ops.business.log.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 应用执行日志结果 DO
 *
 * @author 芋道源码
 */
@TableName("llm_log_app_message")
//@KeySequence("llm_log_app_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAppMessageDO extends TenantBaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 消息uid
     */
    private String uid;
    /**
     * 会话ID
     */
    private String appConversationUid;
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
     * 执行的 app step
     */
    private String appStep;
    /**
     * 执行状态，error：失败，success：成功
     */
    private String status;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * app 配置
     */
    private String variables;
    /**
     * 请求内容
     */
    private String message;
    /**
     * 消耗token数
     */
    private Integer messageTokens;
    /**
     * 消耗token单位价格
     */
    private BigDecimal messageUnitPrice;
    /**
     * 返回内容
     */
    private String answer;
    /**
     * 消耗token数
     */
    private Integer answerTokens;
    /**
     * 消耗token单位价格
     */
    private BigDecimal answerUnitPrice;
    /**
     * 执行耗时
     */
    private Long elapsed;
    /**
     * 总消耗价格
     */
    private BigDecimal totalPrice;
    /**
     * 价格单位
     */
    private String currency;
    /**
     * 执行场景
     */
    private String fromScene;
    /**
     * 临时用户ID
     */
    private String endUser;

}