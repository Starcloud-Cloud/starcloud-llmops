package com.starcloud.ops.business.user.dal.dataObject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("llm_register_users")
@KeySequence("llm_register_users")
@Builder
public class RegisterUserDO extends TenantBaseDO {


    @TableId
    private Long id;

    private String username;

    private String password;

    private String email;

    private Integer status;

    private String registerIp;

    private String activationCode;

    private LocalDateTime registerDate;

    private Long userId;

    private Long inviteUserId;

}
