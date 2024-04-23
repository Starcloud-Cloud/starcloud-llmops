package com.starcloud.ops.business.listing.service.sellersprite.DTO.request;

import lombok.Data;

@Data
public class SellerSpriteResult {

    /**
     * 错误码
     */
    private String code;
    /**
     * 具体信息，含错误原因
     */
    private String message;
    /**
     * 返回数据
     */
    private Object data;

    /**
     * 成功状态
     */
    private Boolean success;


}
