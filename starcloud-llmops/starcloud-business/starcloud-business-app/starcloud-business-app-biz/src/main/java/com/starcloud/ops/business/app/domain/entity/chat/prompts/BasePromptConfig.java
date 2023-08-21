package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import com.starcloud.ops.business.app.domain.entity.config.BaseConfigEntity;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import lombok.Data;

/**
 * 基础 prompt配置类
 */
@Data
public abstract class BasePromptConfig extends BaseConfigEntity {


    @Override
    public void validate() {

        return;
    }


    /**
     * 生成 prompt
     * @return
     */
    protected abstract String _buildPromptStr();

    /**
     * 判断是否激活，是否启用
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


    public String buildPromptStr() {

        if (this.isEnable()) {
            return this._buildPromptStr();
        }

        return "";
    }

}
