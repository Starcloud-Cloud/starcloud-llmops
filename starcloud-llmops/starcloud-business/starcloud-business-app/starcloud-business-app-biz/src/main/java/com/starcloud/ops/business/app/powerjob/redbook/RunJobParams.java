package com.starcloud.ops.business.app.powerjob.redbook;


import lombok.Data;

/**
 * powerjob 执行配置参数对象
 */
@Data
public class RunJobParams {

    /**
     * 执行任务类型：
     * 文案生成
     * 图片生成
     */
    private String runType;


    /**
     * 批量执行一次的数据量
     */
    private Integer bathCount;


    /**
     * 是否只执行重试任务
     */
    private Boolean retryProcess;

    /**
     * 子任务数量
     */
    private Integer subSize;

    /**
     * 是否是测试任务
     */
    private Boolean isTest;
}
