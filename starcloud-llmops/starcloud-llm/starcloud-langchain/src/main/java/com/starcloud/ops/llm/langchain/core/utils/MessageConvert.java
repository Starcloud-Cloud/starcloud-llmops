package com.starcloud.ops.llm.langchain.core.utils;


import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageConvert {

    public static ChatMessage OpenAIMessage(BaseMessage baseMessage) {

        String type = baseMessage.getType();
        if (baseMessage instanceof HumanMessage) {
            type = "user";
        }
        return new ChatMessage(type, baseMessage.getContent());
    }

    public static BaseMessage fixMessage(BaseChatMessage baseMessage) {

        String role = baseMessage.getRole();
        String content = baseMessage.getContent();
        switch (role) {
            case "assistant":
            case "ai":
                return new AIMessage(content);
            case "user":
            case "human":
                return new HumanMessage(content);
            case "function":
                throw new RuntimeException("nonsupport");
            default:
                return new SystemMessage(content);
        }
    }

    public static List<BaseMessage> fixMessageList(List<BaseChatMessage> baseChatMessages) {

        return Optional.ofNullable(baseChatMessages).orElse(new ArrayList<>()).stream().map(MessageConvert::fixMessage).collect(Collectors.toList());
    }

    public static BaseChatMessage fixMessage(BaseMessage baseMessage) {

        BaseChatMessage baseChatMessage = BaseChatMessage.ofRole(baseMessage.getType());
        baseChatMessage.setContent(baseMessage.getContent());
        return baseChatMessage;
    }

    public static List<BaseChatMessage> fixMessage(List<BaseMessage> baseMessages) {

        return Optional.ofNullable(baseMessages).orElse(new ArrayList<>()).stream().map(MessageConvert::fixMessage).collect(Collectors.toList());

    }


}
