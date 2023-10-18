package com.starcloud.ops.business.listing.controller.admin.vo.request;


import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "新建草稿")
public class DraftCreateReqVO {

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "所属站点")
    @NotBlank(message = "站点不能为空")
    private String endpoint;

    @Schema(description = "asin")
    private String asin;

    @Schema(description = "词库Uid")
    private String dictUid;

    @Schema(description = "草稿配置")
    private DraftConfigDTO draftConfig;

    @Schema(description = "五点描述")
    private List<String> fiveDesc;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "搜索词")
    private String searchTerm;

    @Schema(description = "版本")
    private Integer version;

}
