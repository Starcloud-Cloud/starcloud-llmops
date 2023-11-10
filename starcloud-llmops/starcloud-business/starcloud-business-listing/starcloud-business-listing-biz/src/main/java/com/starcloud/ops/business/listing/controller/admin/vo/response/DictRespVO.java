package com.starcloud.ops.business.listing.controller.admin.vo.response;


import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "字典")
public class DictRespVO {

    private Long id;

    private String uid;

    @Schema(description = "词库名称")
    private String name;

    @Schema(description = "站点")
    private String endpoint;

    @Schema(description = "关键词数量")
    private Long count;

    @Schema(description = "关键词摘要")
    private List<String> keywordResume;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    /**
     *
     * {@link com.starcloud.ops.business.listing.enums.AnalysisStatusEnum}
     */
    @Schema(description = "词库状态")
    private String status;
}
