package com.starcloud.ops.business.app.domain.entity.chat.prompts.pre;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.BasePromptConfig;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import lombok.Data;

/**
 * 回复语种 prompt
 */
@Data
public class PreReplyLangPrompt extends BasePromptConfig {

    private String promptV1 = "回复时使用{}进行回复";

    private String promptV2 = "- Identify what language users use in questions and use the same language in your answers. \n" +
            "- Use English or 中文 to answer questions based on the language of the question.";

    private String value;

    public PreReplyLangPrompt(String value) {
        this.value = value;
    }


    @Override
    public void validate(ValidateTypeEnum validateType) {

    }


    @Override
    protected PromptTemplate _buildPrompt() {

        if ("跟随提问".equals(this.value)) {
            return new PromptTemplate(StrUtil.format(this.promptV2, this.value));
        }

        return new PromptTemplate(StrUtil.format(this.promptV1, this.value));
    }


    @Override
    protected Boolean _isEnable() {

        return StrUtil.isNotBlank(this.value);
    }
}
