package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PluginConfigRespVO extends PluginConfigVO {

    private String uid;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;
}
