package com.starcloud.ops.business.dataset.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "")
@Builder
public class SplitForecastResponse {
    private BigDecimal totalPrice;

    private List<SplitDetail> splitList;

}
