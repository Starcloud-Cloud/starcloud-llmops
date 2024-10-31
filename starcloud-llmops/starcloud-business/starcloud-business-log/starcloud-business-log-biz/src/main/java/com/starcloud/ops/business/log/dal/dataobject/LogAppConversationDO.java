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

/**
 * 应用执行日志会话
 *
 * @author admin
 * @version 1.0.0
 * @since 2023-06-08
 */
@TableName("llm_log_app_conversation")
@KeySequence("llm_log_app_conversation_seq")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogAppConversationDO extends DeptBaseDO {

    private static final long serialVersionUID = 3254517515462829485L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话 UID
     */
    private String uid;

    /**
     * 应用 UID
     */
    private String appUid;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用模式
     */
    private String appMode;

    /**
     * 执行场景
     */
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    private String aiModel;
    
    /**
     * 应用配置
     */
    private String appConfig;

    /**
     * 执行状态 ERROR 失败，SUCCESS 成功
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
     * 终端用户 ID (游客)
     */
    private String endUser;

}