package com.starcloud.ops.business.dataset.pojo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文件分段命中测试")
public class MatchByDataSetIdRequest extends BaseQueryRequest{

    @Schema(description = "数据集uid")
    private List<String> datasetUid;

}
