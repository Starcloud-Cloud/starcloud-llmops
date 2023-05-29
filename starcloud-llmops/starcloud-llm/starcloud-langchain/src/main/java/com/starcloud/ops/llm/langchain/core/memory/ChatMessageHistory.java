package com.starcloud.ops.llm.langchain.core.memory;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Data
public class ChatMessageHistory {

    private List<BaseChatMessage> messages = new ArrayList<>();

    public void addUserMessage(String content) {
        this.messages.add(HumanMessage.builder().content(content).build());
    }

    public void addAiMessage(String content) {

        this.messages.add(AIMessage.builder().content(content).build());
    }

    public List<BaseChatMessage> limitMessage(long limit) {

        if (limit >= 0) {
            return Optional.ofNullable(this.messages).orElse(new ArrayList<>()).stream().limit(limit).collect(Collectors.toList());
        } else {
            return Optional.ofNullable(this.messages).orElse(new ArrayList<>()).stream().skip(CollectionUtil.size(this.messages) + limit).collect(Collectors.toList());
        }

    }

}
