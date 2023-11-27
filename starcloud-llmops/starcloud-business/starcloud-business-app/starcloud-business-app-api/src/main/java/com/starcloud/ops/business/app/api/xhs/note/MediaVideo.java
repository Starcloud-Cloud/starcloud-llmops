package com.starcloud.ops.business.app.api.xhs.note;

import lombok.Data;

import java.util.List;

@Data
public class MediaVideo {

    private String md5;

    private Integer hdrType;

    private Integer drmType;

    private List<Integer> streamTypes;

    private Integer bizName;

    private String bizId;

    private Integer duration;

}
