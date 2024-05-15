package com.starcloud.ops.business.app.powerjob.redbook;


import lombok.Data;

import java.io.Serializable;

/**
 * powerjob 执行配置参数对象
 *
 * @author nacoyer
 */
@Data
public class RunJobParams implements Serializable {

    private static final long serialVersionUID = -2759431100922744192L;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 重试次数
     */
    private Integer maxRetry;

    /**
     * 批量执行一次的数据量
     */
    private Integer bathCount;

    /**
     * 子任务数量
     */
    private Integer subSize;

}
