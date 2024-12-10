package com.starcloud.ops.business.user.dal.dataobject.notify;

import lombok.Data;

@Data
public class PurchaseExperienceParamsDTO {

    private Long userId;

    private String nickname;

    private String createTime;

    private Integer daysDiff;
}
