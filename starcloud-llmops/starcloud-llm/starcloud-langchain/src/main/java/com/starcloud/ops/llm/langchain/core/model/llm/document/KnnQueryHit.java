package com.starcloud.ops.llm.langchain.core.model.llm.document;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnnQueryHit {

    private DocumentSegment document;

    private Double score;
}
