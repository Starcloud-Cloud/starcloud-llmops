package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 历史 prompt
 */
@Slf4j
@Data
public class HistoryPrompt extends BasePromptConfig {


    private String promptV1 = "{history}";


    private Boolean hasHistory;

    public HistoryPrompt(Boolean hasHistory) {
        this.hasHistory = hasHistory;
    }


    @Override
    protected String _buildPromptStr() {

        return this.promptV1;
    }

    @Override
    protected Boolean _isEnable() {
        return this.hasHistory;
    }

}
