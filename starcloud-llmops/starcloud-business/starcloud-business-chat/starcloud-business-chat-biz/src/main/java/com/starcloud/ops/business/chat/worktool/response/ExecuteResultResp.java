package com.starcloud.ops.business.chat.worktool.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExecuteResultResp {

    /**
     * 指令明细 json
     */
    private String rawMsg;

    /**
     * 0 为成功
     */
    private Integer rawSuccess;

    /**
     * 机器人id
     */
    private String robotId;

    /**
     * 指令id
     */
    private String messageId;

    /**
     * 执行耗时
     */
    private Double timeCost;

    private String errorReason;

    private LocalDateTime runTime;

    private Integer apiSend;

    private Integer type;

    private List<String> successList;

    private List<String> failList;

}
