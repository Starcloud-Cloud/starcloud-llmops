package com.starcloud.ops.business.user.dal.dataObject;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("llm_recover_password")
@KeySequence("llm_recover_password")
public class RecoverPasswordDO {

    @TableId
    private Long id;

    private Long userId;

    private String email;

    private String recoverCode;

    private Integer status;

    private String recoverIp;

    private LocalDateTime recoverDate;
}
