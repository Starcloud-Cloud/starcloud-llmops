package com.starcloud.ops.business.dataset.pojo.response;

import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文件切割")
@Builder
public class MatchTestResponse {

    private String queryText;

    public List<RecordDTO> records;

}
