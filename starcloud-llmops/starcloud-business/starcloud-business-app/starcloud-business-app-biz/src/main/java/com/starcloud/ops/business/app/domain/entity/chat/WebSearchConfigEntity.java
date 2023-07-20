package com.starcloud.ops.business.app.domain.entity.chat;

import lombok.Data;

@Data
public class WebSearchConfigEntity {

    private Boolean enabled;

    private String whenToUse;

    /**
     * 模型配置
     */
    private String webScope;


}
