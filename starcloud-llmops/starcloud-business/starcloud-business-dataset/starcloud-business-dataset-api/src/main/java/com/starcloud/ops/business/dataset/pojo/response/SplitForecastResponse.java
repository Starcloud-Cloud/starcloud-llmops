package com.starcloud.ops.business.dataset.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "文件切割")
@Builder
public class SplitForecastResponse {
    private BigDecimal totalPrice;

    private List<String> splitList;

    private Long totalTokens;

    private int totalSegment;

}
