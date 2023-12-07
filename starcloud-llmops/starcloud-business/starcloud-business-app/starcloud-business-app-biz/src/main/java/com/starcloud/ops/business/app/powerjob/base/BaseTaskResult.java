package com.starcloud.ops.business.app.powerjob.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 任务执行结果
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-22
 */
@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseTaskResult {

    /**
     * 是否成功
     */
    private boolean success = false;

    /**
     * 执行消息
     */
    private String msg;

    /**
     * key
     */
    private String key;

    /**
     * 异常
     */
    private Throwable throwable;

    /**
     * 构造函数
     *
     * @param success 是否成功
     * @param msg     消息
     */
    public BaseTaskResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    /**
     * 获取结果
     *
     * @param success 是否成功
     * @param msg     消息
     * @return 执行结果
     */
    public static BaseTaskResult of(boolean success, String msg) {
        return BaseTaskResult.builder().success(success).msg(msg).build();
    }
}
