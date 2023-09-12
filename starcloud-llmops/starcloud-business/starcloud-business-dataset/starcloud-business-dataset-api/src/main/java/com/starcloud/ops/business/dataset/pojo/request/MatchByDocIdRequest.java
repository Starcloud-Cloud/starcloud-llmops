package com.starcloud.ops.business.dataset.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "文件分段命中测试")
public class MatchByDocIdRequest  extends BaseQueryRequest{

    @Schema(description = "文档id")
    private List<Long> docId;

}
