package com.starcloud.ops.business.dataset.pojo.response;

import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文件切割")
@Builder
public class MatchQueryVO {

    @Schema(description = "匹配文本")
    private String queryText;

    @Schema(description = "计算queryText消耗的token")
    private Long tokens;

    @Schema(description = "命中的分段")
    public List<RecordDTO> records;

}
