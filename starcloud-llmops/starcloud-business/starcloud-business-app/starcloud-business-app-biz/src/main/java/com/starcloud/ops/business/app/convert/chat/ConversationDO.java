package com.starcloud.ops.business.app.convert.chat;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("llm_conversations")
@KeySequence("llm_conversations")
@Builder
public class ConversationDO extends TenantBaseDO {

    @TableId
    private Long id;

    private Long appId;

    private String inputs;

    private Long accountId;



}
