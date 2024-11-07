package com.starcloud.ops.business.app.model.content;

import lombok.Data;

import java.io.Serializable;

/**
 * 小红书签名
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class RedBookSignature implements Serializable {

    private static final long serialVersionUID = 4932356487329706561L;

    /**
     * 随机字符串
     */
    private String nonce;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 签名
     */
    private String signature;
}
