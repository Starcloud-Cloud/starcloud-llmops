package com.starcloud.ops.business.user.controller.admin.notify.dto;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import lombok.Data;

import java.util.Map;

@Data
public class SendNotifyReqDTO {

    /**
     * 收信人 id
     */
    private Long userId;

    /**
     * 用户类型
     */
    private UserTypeEnum userType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息平台模板code
     */
    private String templateCode;

    /**
     * 模板参数
     */
    private Map<String,Object> params;
}
