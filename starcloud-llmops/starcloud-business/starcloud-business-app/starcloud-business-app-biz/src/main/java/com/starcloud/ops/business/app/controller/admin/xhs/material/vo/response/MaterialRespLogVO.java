package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response;

import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "素材请求生成日志")
public class MaterialRespLogVO {

    @Schema(description = "请求")
    private String requestContent;


    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

}
