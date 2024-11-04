package com.starcloud.ops.business.job.biz.dal.dataobject;

import lombok.Data;

@Data
public class BindAppDetail {
    private Long libraryId;

    private String bindAppUid;

    private String appMarketUid;

    private Integer bindAppType;
}
