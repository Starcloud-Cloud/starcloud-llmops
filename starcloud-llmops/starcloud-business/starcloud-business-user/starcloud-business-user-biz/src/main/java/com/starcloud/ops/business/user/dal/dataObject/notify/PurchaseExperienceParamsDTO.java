package com.starcloud.ops.business.user.dal.dataObject.notify;

import lombok.Data;

@Data
public class PurchaseExperienceParamsDTO {

    private Long userId;

    private String nickname;

    private String createTime;

    private Integer daysDiff;
}
