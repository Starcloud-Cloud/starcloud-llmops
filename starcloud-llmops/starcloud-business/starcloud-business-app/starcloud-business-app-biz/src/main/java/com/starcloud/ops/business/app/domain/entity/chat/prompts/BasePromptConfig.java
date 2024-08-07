package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.config.BaseConfigEntity;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 基础 prompt配置类
 */
@Data
public abstract class BasePromptConfig extends BaseConfigEntity {


    @Override
    public List<Verification> validate(String uid, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }


    /**
     * 生成 prompt
     *
     * @return
     */
    protected abstract PromptTemplate _buildPrompt();

    /**
     * 判断是否激活，是否启用
     *
     * @return
     */
    protected abstract Boolean _isEnable();

    /**
     * 判断是否激活，是否启用
     *
     * @return
     */
    public Boolean isEnable() {
        return this._isEnable();
    }


    public PromptTemplate buildPrompt() {

        return this._buildPrompt();

    }

    public String buildPromptStr() {

        if (this.isEnable() && this.buildPrompt() != null) {
            PromptValue promptValue = this.buildPrompt().formatPrompt();
            return promptValue.toStr();
        }

        return "";
    }


    public String buildPromptStr(Boolean addN) {

        String str = this.buildPromptStr();
        if (StrUtil.isNotBlank(str)) {
            if (addN) {
                return str + "\n";
            }
        }

        return str;
    }

}
