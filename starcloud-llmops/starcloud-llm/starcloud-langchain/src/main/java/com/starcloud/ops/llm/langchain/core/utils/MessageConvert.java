package com.starcloud.ops.llm.langchain.core.utils;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageConvert {


    public static BaseChatMessage fixMessage(BaseMessage baseMessage) {

        BaseChatMessage baseChatMessage = BaseChatMessage.ofRole(baseMessage.getType());
        baseChatMessage.setContent(baseMessage.getContent());
        return baseChatMessage;
    }

    public static List<BaseChatMessage> fixMessage(List<BaseMessage> baseMessages) {

        return Optional.ofNullable(baseMessages).orElse(new ArrayList<>()).stream().map(MessageConvert::fixMessage).collect(Collectors.toList());

    }



}
