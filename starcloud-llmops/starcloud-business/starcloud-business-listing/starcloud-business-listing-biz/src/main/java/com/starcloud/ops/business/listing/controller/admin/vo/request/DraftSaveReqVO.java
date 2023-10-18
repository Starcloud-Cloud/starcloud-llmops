package com.starcloud.ops.business.listing.controller.admin.vo.request;

import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "保存新版草稿")
public class DraftSaveReqVO {

    @Schema(description = "草稿Uid")
    @NotBlank(message = "草稿uid不能为空")
    private String uid;

    @Schema(description = "版本")
    @Min(value = 1, message = "草稿版本必须大于零")
    private Integer version;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "所属站点")
    private String endpoint;

    @Schema(description = "asin")
    private String asin;

    @Schema(description = "关键词摘要")
    private List<String> keywordResume;

    @Schema(description = "草稿配置")
    private DraftConfigDTO draftConfig;

    @Schema(description = "五点描述")
    private List<String> fiveDesc;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "搜索词")
    private String searchTerm;


}
