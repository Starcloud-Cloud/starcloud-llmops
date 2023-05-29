package com.starcloud.ops.llm.langchain.core.model.chat.base.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class SystemMessage extends BaseChatMessage {

    @Builder.Default
    private String role = "system";

}
