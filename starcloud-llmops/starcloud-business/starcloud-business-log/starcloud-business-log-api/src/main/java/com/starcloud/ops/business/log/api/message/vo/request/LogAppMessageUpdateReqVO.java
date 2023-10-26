package com.starcloud.ops.business.log.api.message.vo.request;

import com.starcloud.ops.business.log.api.message.vo.LogAppMessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志结果更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageUpdateReqVO extends LogAppMessageBaseVO {

    private static final long serialVersionUID = -4773057567864198605L;

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

}