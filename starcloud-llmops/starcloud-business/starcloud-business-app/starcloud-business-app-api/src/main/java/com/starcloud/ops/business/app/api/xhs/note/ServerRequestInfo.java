package com.starcloud.ops.business.app.api.xhs.note;

import lombok.Data;

@Data
public class ServerRequestInfo {

    private String state;

    private Integer errorCode;

    private String errMsg;

    private NoteDetail noteDetail;
}
