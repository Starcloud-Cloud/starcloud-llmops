package com.starcloud.ops.business.job.biz.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobLogDTO {
    private String appName;

    private String appUid;

    private String appMarketUid;

    private Integer bindAppType;

    private String pluginName;

    private String libraryName;

    private Long libraryId;

    private String type;

    private Integer triggerType;

    private Boolean success;

    private int count;

    private String executeResult;

    private Long executeTime;

    private LocalDateTime triggerTime;

    private String creator;

}
