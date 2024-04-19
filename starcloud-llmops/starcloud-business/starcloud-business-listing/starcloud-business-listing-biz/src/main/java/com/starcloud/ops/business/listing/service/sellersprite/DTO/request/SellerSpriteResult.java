package com.starcloud.ops.business.listing.service.sellersprite.DTO.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private T data;

    /**
     * 成功状态
     */
    private Boolean success;


    public static boolean isSuccess(Boolean success) {
        return success;
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isSuccess() {
        return isSuccess(success);
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

}
