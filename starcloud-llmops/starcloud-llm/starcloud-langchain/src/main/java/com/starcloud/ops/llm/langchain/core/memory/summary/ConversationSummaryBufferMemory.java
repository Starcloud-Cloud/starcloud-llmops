package com.starcloud.ops.llm.langchain.core.memory.summary;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationSummaryBufferMemory extends SummarizerMixin {

    private String movingSummaryBuffer = "";

    private Integer maxTokenLimit = 2000;

    public ConversationSummaryBufferMemory(BaseLLM llm, Integer maxTokenLimit) {
        super(llm);
        this.maxTokenLimit = maxTokenLimit;
    }


    protected List<BaseChatMessage> getBuffer() {
        return this.getChatHistory().getMessages();
    }


    @SneakyThrows
    @Override
    public List<BaseVariable> loadMemoryVariables() {

        List<BaseChatMessage> messages = this.getBuffer();

        if (StrUtil.isNotBlank(this.movingSummaryBuffer)) {
            BaseChatMessage firstMessages = this.createSummaryMessage(this.movingSummaryBuffer);
            messages.add(0, firstMessages);
        }

        if (this.getReturnMessages()) {

            return Arrays.asList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(messages)
                    .build());
        } else {
            return Arrays.asList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(getBufferString(messages))
                    .build());

        }
    }

    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        super.saveContext(baseVariables, result);

        List<BaseChatMessage> messages = this.getBuffer();

        Long sum = this.getLlm().getNumTokensFromMessages(messages);

        if (sum > this.maxTokenLimit) {

            List<BaseChatMessage> prunedMemory = new ArrayList<>();

            while (sum > this.maxTokenLimit) {
                BaseChatMessage firstMessage = this.getBuffer().remove(0);

                prunedMemory.add(firstMessage);
                prunedMemory.add(this.getBuffer().remove(0));
                sum = this.getLlm().getNumTokensFromMessages(this.getBuffer());
            }

            BaseLLMResult baseLLMResult = this.predictNewSummary(prunedMemory, this.movingSummaryBuffer);
            this.movingSummaryBuffer = baseLLMResult.getText();
        }
    }


}
