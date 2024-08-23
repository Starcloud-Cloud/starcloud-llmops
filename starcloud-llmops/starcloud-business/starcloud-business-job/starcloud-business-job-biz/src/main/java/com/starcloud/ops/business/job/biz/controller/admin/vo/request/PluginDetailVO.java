package com.starcloud.ops.business.job.biz.controller.admin.vo.request;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobConfigBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.JOB_CONFIG_ERROR;

@Data
@Schema(description = "插件详情")
public class PluginDetailVO extends JobConfigBaseVO {

    /**
     * 素材库uid
     */
    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid不能为空")
    private String libraryUid;

    @Schema(description = "插件uid")
    @NotBlank(message = "插件uid不能为空")
    private String pluginUid;

    @Schema(description = "插件名称")
    @NotBlank(message = "插件名称不能为空")
    private String pluginName;

    /**
     * 字段映射
     */
    @Schema(description = "字段映射配置")
    @NotBlank(message = "字段映射配置不能为空")
    private String fieldMap;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数配置")
    private String executeParams;

    @Override
    public void valid() {
        super.valid();
        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
        };
        Map<String, String> fieldMap = JSON.parseObject(this.fieldMap, typeReference.getType());
        if (CollectionUtil.isEmpty(fieldMap)) {
            throw exception(JOB_CONFIG_ERROR, "", "字段映射未配置");
        }

    }
}
