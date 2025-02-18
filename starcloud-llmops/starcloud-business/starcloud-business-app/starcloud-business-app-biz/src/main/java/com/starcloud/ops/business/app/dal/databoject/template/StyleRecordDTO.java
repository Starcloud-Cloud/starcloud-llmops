package com.starcloud.ops.business.app.dal.databoject.template;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StyleRecordDTO {

    private String uid;

    private String styleUid;

    private String planUid;

    private String imageStyleList;

    private LocalDateTime createTime;
}
