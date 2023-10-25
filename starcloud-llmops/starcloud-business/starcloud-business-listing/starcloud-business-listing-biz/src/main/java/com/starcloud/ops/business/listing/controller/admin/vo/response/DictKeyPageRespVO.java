package com.starcloud.ops.business.listing.controller.admin.vo.response;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "词库关键词分页")
public class DictKeyPageRespVO {

    @Schema(description = "词库状态")
    private String status;

    @Schema(description = "分页数据")
    private PageResult<KeywordMetadataRespVO> keywordMetadataResp;
}
