package com.starcloud.ops.business.app.domain.entity.chat.prompts.pre;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.chat.prompts.BasePromptConfig;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 最大回复字数 prompt
 */
@Data
public class PreMaxReturnPrompt extends BasePromptConfig {

    //@todo 可以再增加 系统 prompt
    private String promptV1 = "回复长度最好不要超过{}字";

    private Integer value;

    public PreMaxReturnPrompt(Integer value) {
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

        return this.value != null && this.value > 0;
    }
}
