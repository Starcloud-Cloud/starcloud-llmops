package com.starcloud.ops.business.log.dal.dataobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogAppConversationInfoPO {


    private String uid;


    private String appUid;


    private String appMode;


    private String appName;


    private String fromScene;

    /**
     * 总消耗token数
     */
    private Integer totalMessageTokens = 0;

    /**
     * 总消耗token数
     */
    private Integer totalAnswerTokens = 0;


    private Integer messageCount = 0;

    private Integer feedbacksCount = 0;

    private Long totalElapsed = 0L;

    private BigDecimal totalPrice = BigDecimal.ZERO;


    private String status;


    private String creator;


    private String endUser;


    private LocalDateTime createTime;
}
