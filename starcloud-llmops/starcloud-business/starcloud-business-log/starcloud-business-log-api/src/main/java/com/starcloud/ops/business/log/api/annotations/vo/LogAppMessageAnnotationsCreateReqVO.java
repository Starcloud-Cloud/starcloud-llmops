package com.starcloud.ops.business.log.api.annotations.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 应用执行日志结果反馈标注创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageAnnotationsCreateReqVO extends LogAppMessageAnnotationsBaseVO {

}
