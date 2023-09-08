package com.starcloud.ops.business.share.dal.dataobject;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("llm_share_conversation")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShareConversationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * uid
     */
    private String uid;

    /**
     * key
     */
    private String shareKey;

    /**
     * app uid
     */
    private String appUid;

    /**
     * 媒介id
     */
    private String mediumUid;

    /**
     * 会话uid
     */
    private String conversationUid;

    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

    /**
     * 启用/禁用
     */
    private Boolean disabled;

    /**
     * 游客
     */
    private Boolean endUser;


}
