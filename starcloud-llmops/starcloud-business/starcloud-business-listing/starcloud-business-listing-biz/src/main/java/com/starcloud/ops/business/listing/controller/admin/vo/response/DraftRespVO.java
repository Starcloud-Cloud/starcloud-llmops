package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "草稿详情")
public class DraftRespVO {

    private Long id;

    @Schema(description = "草稿Uid")
    private String uid;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "所属站点")
    private String endpoint;

    @Schema(description = "asin")
    private String asin;

    @Schema(description = "关键词摘要")
    private List<KeywordResumeDTO> keywordResume;

    @Schema(description = "关键词元数据")
    private List<KeywordMetaDataDTO> keywordMetaData;

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
