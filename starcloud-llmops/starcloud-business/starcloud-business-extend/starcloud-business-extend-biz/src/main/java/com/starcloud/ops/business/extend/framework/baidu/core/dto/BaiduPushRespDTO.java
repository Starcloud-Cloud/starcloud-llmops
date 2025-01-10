package com.starcloud.ops.business.extend.framework.baidu.core.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 快递查询的轨迹 Resp DTO
 *
 * @author jason
 */
@Data
public class BaiduPushRespDTO {

    /**
     * 发生时间
     */
    private LocalDateTime time;

    /**
     * 快递状态
     */
    private String success;

}
