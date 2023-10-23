package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "修改词库")
public class DictModifyReqVO {

    @Schema(description = "词库uid")
    @NotBlank(message = "词库uid不能为空")
    private String uid;

    @Schema(description = "词库名称")
    private String name;

    @Schema(description = "所有关键字")
    private List<String> keywordResume;

    @Schema(description = "状态")
    private Boolean enable;

}
