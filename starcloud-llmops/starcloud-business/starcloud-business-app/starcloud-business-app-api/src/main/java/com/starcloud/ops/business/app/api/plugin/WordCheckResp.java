package com.starcloud.ops.business.app.api.plugin;

import lombok.Data;

@Data
public class WordCheckResp {

    private String code;

    private String msg;

    private WordCheckContent data;
}
