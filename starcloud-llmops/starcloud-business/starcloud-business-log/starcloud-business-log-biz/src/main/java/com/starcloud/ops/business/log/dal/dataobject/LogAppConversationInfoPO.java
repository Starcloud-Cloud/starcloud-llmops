package com.starcloud.ops.business.log.dal.dataobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    private Integer messageCount;

    private Integer feedbacksCount;


    private Long elapsedTotal;


    private String status;


    private String creator;


    private String endUser;


    private LocalDateTime createTime;
}
