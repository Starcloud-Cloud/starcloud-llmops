package com.starcloud.ops.business.log.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 应用执行日志结果 DO
 *
 * @author admin
 */
@TableName("llm_log_app_message")
@KeySequence("llm_log_app_message_seq")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogAppMessageDO extends DeptBaseDO {

    private static final long serialVersionUID = 9210036499937789257L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息 UID
     */
    private String uid;

    /**
     * 会话 UID
     */
    private String appConversationUid;

    /**
     * 应用 UID
     */
    private String appUid;

    /**
     * 应用 模式
     */
    private String appMode;

    /**
     * 执行的应用步骤
     */
    private String appStep;

    /**
     * 执行场景
     */
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    private String aiModel;

    /**
     * 临时用户ID
     */
    private String endUser;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 渠道uid
     */
    private String mediumUid;

    /**
     * 执行状态，ERROR：失败，SUCCESS：成功
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
     * 应用配置
     */
    private String variables;

    /**
     * 应用配置
     */
    private String appConfig;

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
     * 总消耗价格
     */
    private BigDecimal totalPrice;

    /**
     * 价格单位
     */
    private String currency;

    /**
     * 消耗积分
     */
    private Integer costPoints;

    /**
     * 消耗图片点数
     */
    private Integer imagePoints;

    /**
     * 执行耗时
     */
    private Long elapsed;

}