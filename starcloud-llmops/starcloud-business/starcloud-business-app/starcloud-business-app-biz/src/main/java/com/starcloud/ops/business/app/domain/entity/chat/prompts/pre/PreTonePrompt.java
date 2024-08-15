package com.starcloud.ops.business.app.domain.entity.chat.prompts.pre;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.BasePromptConfig;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import lombok.Data;

/**
 * 语气 prompt
 */
@Data
public class PreTonePrompt extends BasePromptConfig {


    //@todo 可以再增加 系统 prompt
    private String promptV1 = "请使用{}语气跟我进行对话";

    private String value;

    public PreTonePrompt(String value) {
        this.value = value;
    }


    @Override
    public void validate(ValidateTypeEnum validateType) {

    }

    @Override
    protected PromptTemplate _buildPrompt() {
        return new PromptTemplate(StrUtil.format(this.promptV1, this.value));
    }

    @Override
    protected Boolean _isEnable() {

        return StrUtil.isNotBlank(this.value);
    }
}
