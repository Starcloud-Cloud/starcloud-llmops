package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(defaultValue = "修改创作内容")
public class CreativeContentModifyReqVO {


    @Schema(description = "创作计划uid")
    @NotBlank(message = "创作计划uid 不能为空")
    private String planUid;

    @Schema(description = "业务uid")
    @NotBlank(message = "业务uid 不能为空")
    private String businessUid;

    @Schema(description = "文案标题")
    private String copyWritingTitle;

    @Schema(description = "文案内容")
    private String copyWritingContent;

    @Schema(description = "图片结果")
    private List<CreativeImageDTO> pictureContent;
}
