package com.starcloud.ops.business.log.dal.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogAppMessageStatisticsListPO {


    private String appUid;


    private String appMode;


    private String appName;

    private String fromScene;


    private Integer messageCount;

    private Integer successCount;

    private Integer errorCount;

    private Integer userCount;

    private BigDecimal elapsedTotal;

    private BigDecimal elapsedAvg;


    private Integer messageTokens;


    private Integer answerTokens;

    private Integer tokens;

    private String createDate;

}
