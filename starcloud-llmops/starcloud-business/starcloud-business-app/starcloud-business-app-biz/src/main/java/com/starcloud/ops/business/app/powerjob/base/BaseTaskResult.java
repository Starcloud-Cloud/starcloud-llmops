package com.starcloud.ops.business.app.powerjob.base;

import lombok.*;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseTaskResult {

    /**
     * key
     */
    private String key;

    /**
     * 是否成功
     */
    private boolean success = false;

    /**
     * 消息
     */
    private String msg;

    /**
     * 异常
     */
    private Throwable throwable;

    public BaseTaskResult(String key, boolean success) {
        this.key = key;
        this.success = success;
    }

    public BaseTaskResult(String key, boolean success, String mgs) {
        this.key = key;
        this.success = success;
        this.msg = mgs;
    }

    public BaseTaskResult(boolean success) {
        this.success = success;
    }

    public BaseTaskResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static BaseTaskResult of(boolean success, String msg) {
        return new BaseTaskResult(success, msg);
    }
}
