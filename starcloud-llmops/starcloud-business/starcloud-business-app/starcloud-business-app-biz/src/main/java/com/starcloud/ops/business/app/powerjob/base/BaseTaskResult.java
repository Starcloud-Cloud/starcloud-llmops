package com.starcloud.ops.business.app.powerjob.base;

import lombok.*;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseTaskResult {

    private String key;
    private boolean success = false;
    private String msg;
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
}
