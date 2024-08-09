package com.starcloud.ops.business.app.controller.admin.plugins.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Schema(description = "插件配置")
public class PluginConfigVO {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

    /**
     * 素材库uid
     */
    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid 不能为空")
    private String libraryUid;

    @Schema(description = "插件uid")
    @NotBlank(message = "插件uid 不能为空")
    private String pluginUid;

    /**
     * 字段映射
     */
    @Schema(description = "字段映射配置")
    private String fieldMap;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数配置")
    private String executeParams;
}
