package com.starcloud.ops.llm.langchain.core.model.llm.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseGeneration<R> {

    private String text;

    private R generationInfo;

}
