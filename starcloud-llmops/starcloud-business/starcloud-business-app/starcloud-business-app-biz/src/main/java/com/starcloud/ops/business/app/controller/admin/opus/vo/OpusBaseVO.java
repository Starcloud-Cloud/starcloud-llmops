package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "作品集")
public class OpusBaseVO {

    @Schema(description = "作品集名称")
    @NotBlank(message = "作品集名称必填")
    @Length(min = 4, max = 16, message = "作品集名称长度为 4-16 位")
    private String opusName;

    @Schema(description = "作品集描述")
    @Length(max = 32, message = "作品集描述长度为 0-32 位")
    private String opusDesc;

    @Schema(description = "作品集类型")
    @NotBlank(message = "作品集类型必填")
    private String opusType;

    @Schema(description = "作品集图片")
    private List<String> opusImages;
}
