package com.starcloud.ops.business.app.dal.databoject.config;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_chat_expand_config")
public class ChatExpandConfigDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    private String appConfigId;


    /**
     * 配置类型
     */
    private Integer type;

    /**
     * 开启/关闭
     */
    private Boolean disabled;

    /**
     * 配置内容
     */
    private String config;


}
