package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.domain.entity.chat.ModelConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.service.chat.momory.ConversationSummaryDbMessageMemory;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.memory.template.ChatMemoryPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 聊天 的整体 prompt
 */
@Data
public class ChatPrompt extends BasePromptConfig {

    private Boolean toolPrompt;

    private String promptV1 = "{ContextPrompt}\n" +
            "Below is the history of the conversation, please answer after referring to all the requirements.\n\n{HistoryPrompt}" +
            "Human: {input}\n" +
            "AI: \n";

    private String promptVTool = "{ContextPrompt}\n" +
            "Below is the history of the conversation, please answer after referring to all the requirements.\n\n[History Start]{HistoryPrompt}" +
            "Human: {input}\n" +
            //工具调用历史，只显示一次完整工具调用的历史，不包含用户输入
            "{" + BaseSingleActionAgent.TEMP_VARIABLE_SCRATCHPAD + "}" +
            "AI: \n";


    //只有用户输入后
    private String promptV3 = "{input}";

    private Boolean gptMessage = false;

    private ChatPrePrompt chatPrePrompt;

    private ContextPrompt contextPrompt;

    private HistoryPrompt historyPrompt;

    public ChatPrompt(ChatPrePrompt chatPrePrompt, ContextPrompt contextPrompt, HistoryPrompt historyPrompt) {
        this.chatPrePrompt = chatPrePrompt;
        this.contextPrompt = contextPrompt;
        this.historyPrompt = historyPrompt;
    }


    public ChatPromptTemplate buildChatPromptTemplate(Boolean toolPrompt) {

        this.toolPrompt = toolPrompt;
        List<BaseMessagePromptTemplate> messagePromptTemplates = new ArrayList<>();

        //prePrompt 放到system里面
        messagePromptTemplates.add(new SystemMessagePromptTemplate(this.chatPrePrompt.buildPrompt()));
        messagePromptTemplates.add(new HumanMessagePromptTemplate(this.buildPrompt()));

        return ChatPromptTemplate.fromMessages(messagePromptTemplates);
    }

    public ChatPromptTemplate buildChatPromptTemplate(ConversationSummaryDbMessageMemory messageMemory) {

        SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(this.chatPrePrompt.buildPromptWithContent(this.contextPrompt));
        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(this.buildPrompt());

        //增加历史记录
        return ChatMemoryPromptTemplate.fromMessages(systemMessagePromptTemplate, humanMessagePromptTemplate, messageMemory);
    }

    /**
     * 获取模型设剩余最大可用 tokens
     * 扣除 前缀prompt + 上下文+  设置返回tokens + query
     *
     * @return
     */
    public int calculateModelUseMaxToken(ModelConfigEntity modelConfig, String userQuery) {

        String prePrompt = this.chatPrePrompt.buildPromptStr();
        String dataSetStr = this.contextPrompt.buildPromptStr();
        OpenaiCompletionParams completionParams = modelConfig.getCompletionParams();

        Optional<ModelType> optionalModel = ModelType.fromName(completionParams.getModel());
        ModelType modelType = ModelType.GPT_3_5_TURBO;
        if (optionalModel.isPresent()) {
            modelType = optionalModel.get();
        }
        int maxTokens = modelType.getMaxContextLength();
        if (modelConfig.getMaxSummaryTokens() != null) {
            maxTokens = modelConfig.getMaxSummaryTokens();
        }

        if (StringUtils.isNotBlank(prePrompt)) {
            maxTokens -= TokenUtils.intTokens(modelType, prePrompt);
        }
        if (StringUtils.isNotBlank(dataSetStr)) {
            maxTokens -= TokenUtils.intTokens(modelType, dataSetStr);
        }

        //扣除用户设置的最大返回结果的 tokens
        if (completionParams.getMaxTokens() != null && completionParams.getMaxTokens() > 0) {
            // 临界值时 总结会多最后一次对话的回复 预先扣除token 10%
            maxTokens -= completionParams.getMaxTokens() * 1.1;
        } else {
            maxTokens -= 550 * 1.1;
        }

        maxTokens -= TokenUtils.intTokens(modelType, userQuery);
        return maxTokens;

    }

    @Override
    protected Boolean _isEnable() {
        return this.chatPrePrompt != null && this.contextPrompt != null;
    }

    @Override
    protected PromptTemplate _buildPrompt() {
        List<BaseVariable> variables = new ArrayList<>();

        if (this.gptMessage) {
            return new PromptTemplate(this.promptV3, variables);
        }

        variables.add(BaseVariable.newTemplate("ContextPrompt", this.contextPrompt.buildPrompt()));
        variables.add(BaseVariable.newString("HistoryPrompt", this.historyPrompt.buildPromptStr(true)));
        PromptTemplate template;
        if (this.toolPrompt) {
            template = new PromptTemplate(this.promptVTool, variables);
        } else {
            template = new PromptTemplate(this.promptV1, variables);
        }

        return template;
    }

}
