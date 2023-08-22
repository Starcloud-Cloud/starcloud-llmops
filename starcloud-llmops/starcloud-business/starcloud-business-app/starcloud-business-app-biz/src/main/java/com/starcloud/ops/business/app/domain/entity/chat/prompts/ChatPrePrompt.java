package com.starcloud.ops.business.app.domain.entity.chat.prompts;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.chat.PrePromptConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.pre.PreMaxReturnPrompt;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.pre.PreReplyLangPrompt;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.pre.PreTonePrompt;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.Arrays;

/**
 * 聊天的 前置初始化 prompt
 */
@Data
public class ChatPrePrompt extends BasePromptConfig {

    private String userPrompt;

    private PrePromptConfigEntity prePromptConfig;

    private PreTonePrompt tonePrompt;

    private PreMaxReturnPrompt maxReturnPrompt;

    private PreReplyLangPrompt replyLangPrompt;

    private String promptV1 = "{UserPrompt}\n" +
            "{PreTonePrompt}{PreMaxReturnPrompt}{PreReplyLangPrompt}";

    public ChatPrePrompt(String userPrompt, PrePromptConfigEntity prePromptConfigEntity) {
        this.userPrompt = userPrompt;
        this.prePromptConfig = prePromptConfigEntity;

        this.tonePrompt = new PreTonePrompt(prePromptConfigEntity.getTone());
        this.maxReturnPrompt = new PreMaxReturnPrompt(prePromptConfigEntity.getMaxReturn());
        this.replyLangPrompt = new PreReplyLangPrompt(prePromptConfigEntity.getReplyLang());
    }


    @Override
    public void validate() {

    }

    @Override
    protected String _buildPromptStr() {

        BaseVariable variable = BaseVariable.newString("UserPrompt", this.userPrompt);
        BaseVariable tone = BaseVariable.newString("PreTonePrompt", this.tonePrompt.buildPromptStr(true));
        BaseVariable maxReturn = BaseVariable.newString("PreMaxReturnPrompt", this.maxReturnPrompt.buildPromptStr(true));
        BaseVariable replyLang = BaseVariable.newString("PreReplyLangPrompt", this.replyLangPrompt.buildPromptStr(true));

        PromptTemplate template = new PromptTemplate(this.promptV1);

        return template.format(Arrays.asList(variable, tone, maxReturn, replyLang));
    }

    @Override
    protected Boolean _isEnable() {

        return StrUtil.isNotBlank(this.userPrompt);
    }
}
