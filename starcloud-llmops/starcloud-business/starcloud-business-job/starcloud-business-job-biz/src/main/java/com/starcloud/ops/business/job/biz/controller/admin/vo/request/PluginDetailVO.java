package com.starcloud.ops.business.job.biz.controller.admin.vo.request;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobConfigBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.FIELD_VALID_ERROR;

@Data
@Schema(description = "插件详情")
public class PluginDetailVO extends JobConfigBaseVO {

    /**
     * 素材库uid
     */
    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid 不能为空")
    private String libraryUid;

    @Schema(description = "插件uid")
    @NotBlank(message = "插件uid 不能为空")
    private String pluginUid;

    @Schema(description = "插件名称")
    @NotBlank(message = "插件名称 不能为空")
    private String pluginName;

    /**
     * 字段映射
     */
    @Schema(description = "字段映射配置")
    @NotBlank(message = "字段映射配置 不能为空")
    private String fieldMap;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数配置")
    private String executeParams;

}
