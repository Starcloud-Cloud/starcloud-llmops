package com.starcloud.ops.llm.langchain.core.model.chat.base.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseChatMessage {

    private String role;

    private String content;

    private Long tokens;

    public BaseChatMessage(String content) {
        this.content = content;
    }


    public static BaseChatMessage ofRole(String role) {

        switch (role) {
            case "assistant":
                return AIMessage.builder().build();
            case "user":
                return HumanMessage.builder().build();
            default:
                return SystemMessage.builder().build();
        }

    }
}
