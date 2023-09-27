package com.starcloud.ops.business.app.domain.entity.chat.prompts;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.domain.entity.chat.ModelConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.service.chat.momory.ConversationSummaryDbMessageMemory;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.memory.template.ChatMemoryPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 聊天 的整体 prompt
 */
@Data
public class ChatPrompt extends BasePromptConfig {

    private String promptV1 = "System Time: {NowTime}\n" +
            "{ContextPrompt}\n" +
            "Below is the history of the conversation, please answer after referring to all the requirements.\n\n{HistoryPrompt}" +
            "Human: {input}\n" +
            "AI: \n";

    private String promptVTool = "System Time: {NowTime}\n" +
            "{ContextPrompt}\n" +
            "Below is the history of the conversation, please answer after referring to all the requirements.\n\n[History Start]{HistoryPrompt}" +
            "Human: {input}\n" +
            //工具调用历史，只显示一次完整工具调用的历史，不包含用户输入
            "{" + BaseSingleActionAgent.TEMP_VARIABLE_SCRATCHPAD + "}" +
            "AI: \n";


    private String promptMaster = "Current system time: {NowTime}.\n" +
            "{PrePrompt}\n" +
            "{ContextPrompt}\n";


    //只有用户输入后
    private String promptV3 = "{input}";

    private ChatPrePrompt chatPrePrompt;

    private ContextPrompt contextPrompt;

    @Deprecated
    private HistoryPrompt historyPrompt;

    public ChatPrompt(ChatPrePrompt chatPrePrompt, ContextPrompt contextPrompt, HistoryPrompt historyPrompt) {
        this.chatPrePrompt = chatPrePrompt;
        this.contextPrompt = contextPrompt;
        this.historyPrompt = historyPrompt;
    }


    @Deprecated
    public ChatPromptTemplate buildChatPromptTemplate(Boolean toolPrompt) {

        List<BaseMessagePromptTemplate> messagePromptTemplates = new ArrayList<>();

        //prePrompt 放到system里面
        messagePromptTemplates.add(new SystemMessagePromptTemplate(this.buildPrompt()));

        StringPromptTemplate stringPromptTemplate = new PromptTemplate(this.promptV3, new ArrayList<>());
        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(stringPromptTemplate);
        messagePromptTemplates.add(humanMessagePromptTemplate);

        return ChatPromptTemplate.fromMessages(messagePromptTemplates);
    }

    public ChatPromptTemplate buildChatPromptTemplate(ConversationSummaryDbMessageMemory messageMemory) {

        SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(this.buildPrompt());

        //写死一个
        StringPromptTemplate stringPromptTemplate = new PromptTemplate(this.promptV3, new ArrayList<>());
        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(stringPromptTemplate);

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

        Optional<ModelTypeEnum> optionalModel = ModelTypeEnum.fromName(completionParams.getModel());

        //避免GPT4这种模型，Maxtoken太多才进行总结
        ModelTypeEnum modelType = ModelTypeEnum.GPT_3_5_TURBO;
//        if (optionalModel.isPresent()) {
//            modelType = optionalModel.get();
//        }

        int maxTokens = modelType.getMaxContextLength();
        if (modelConfig.getMaxSummaryTokens() != null) {
            maxTokens = modelConfig.getMaxSummaryTokens();
        }

        if (StringUtils.isNotBlank(prePrompt)) {
            maxTokens -= TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, prePrompt);
        }
        if (StringUtils.isNotBlank(dataSetStr)) {
            maxTokens -= TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, dataSetStr);
        }

        //扣除用户设置的最大返回结果的 tokens
        if (completionParams.getMaxTokens() != null && completionParams.getMaxTokens() > 0) {
            // 临界值时 总结会多最后一次对话的回复 预先扣除token 10%
            maxTokens -= completionParams.getMaxTokens() * 1.1;
        } else {
            maxTokens -= 550 * 1.1;
        }

        maxTokens -= TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, userQuery);
        return maxTokens;

    }

    @Override
    protected Boolean _isEnable() {
        return true;
    }

    @Override
    protected PromptTemplate _buildPrompt() {
        List<BaseVariable> variables = new ArrayList<>();

        variables.add(BaseVariable.newString("NowTime", (new Date()).toString()));
        variables.add(BaseVariable.newTemplate("PrePrompt", this.chatPrePrompt.buildPrompt()));
        variables.add(BaseVariable.newTemplate("ContextPrompt", this.contextPrompt.buildPrompt()));

        return new PromptTemplate(this.promptMaster, variables);
    }

}
