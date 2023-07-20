package com.starcloud.ops.business.app.domain.llm;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;

public class PromptTemplateConfig {


    public static String webSearchPrePrompt(WebSearchConfigEntity webSearchConfig) {

        String tmp = "Note when you <{}>  use this tool.";
        String tmp2 = "Note the url you want to query need to be within those domains <{}>.";

        String result = "";
        if (StrUtil.isNotBlank(webSearchConfig.getWhenToUse())) {
            result += StrUtil.format(tmp, webSearchConfig.getWhenToUse());
        }

        if (!"*".equals(webSearchConfig.getWebScope())) {
            result += StrUtil.format(tmp2, webSearchConfig.getWebScope());
        }

        return result;
    }

}
