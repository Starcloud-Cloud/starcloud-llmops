package com.starcloud.ops.business.app.feign.dto.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "视频生成返回结果")
public class VideoGeneratorResult implements java.io.Serializable {

    private static final long serialVersionUID = -1181017995368174414L;

    /**
     * 任务编号
     */
    @Schema(description = "任务编号")
    private String taskId;

}
