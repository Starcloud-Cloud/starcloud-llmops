package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftItemScoreDTO;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private List<String> keywordResume;

    @Schema(description = "关键词数据")
    private List<KeywordMetaDataDTO> keywordMetaData;

    @Schema(description = "草稿配置")
    private DraftConfigDTO draftConfig;

    @Schema(description = "草稿分数")
    private Double score;

    @Schema(description = "命中搜索量")
    private Long matchSearchers;

    @Schema(description = "总搜索量")
    private Long totalSearches;

    @Schema(description = "各项分数")
    private DraftItemScoreDTO itemScore;

    @Schema(description = "五点描述")
    private Map<String,String> fiveDesc;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "搜索词")
    private String searchTerm;

    @Schema(description = "版本")
    private Integer version;

    /**
     * {@link com.starcloud.ops.business.listing.enums.AnalysisStatusEnum}
     */
    @Schema(description = "草稿状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;
}
