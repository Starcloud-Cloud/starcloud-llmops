package com.starcloud.ops.business.open.api.dto;


import lombok.Data;

@Data
public class WeChatRequestDTO {

    private String query;

    /**
     * 用户微信openId
     */
    private String fromUser;
}
